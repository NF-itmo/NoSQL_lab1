package org.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.Lock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EtcdConcurrentWritesIT {
    private static final int TEST_BOOK_ID = 42;
    private static final int INCREMENTS = 100;
    private static final int WORKERS = 20;
    private static final Duration TIMEOUT = Duration.ofSeconds(30);

    private static final ByteSequence AVAILABILITY_KEY = bytes("/books/availability/" + TEST_BOOK_ID);
    private static final ByteSequence VIEWS_KEY = bytes("/books/views/" + TEST_BOOK_ID);
    private static final ByteSequence BOOK_LOCK = bytes("/locks/books/" + TEST_BOOK_ID);

    private static Client client;
    private static KV kvClient;
    private static Lock lockClient;
    private static Lease leaseClient;
    private static BooksRepository booksRepository;

    @BeforeAll
    static void connectToEtcd() throws Exception {
        Logger.getLogger("io.grpc.netty.TcpMetrics").setLevel(Level.WARNING);

        final String configuredEndpoints = System.getenv().getOrDefault(
                "ETCD_ENDPOINTS",
                "http://localhost:2379,http://localhost:22379,http://localhost:32379"
        );

        client = Client.builder()
                .endpoints(configuredEndpoints.split(","))
                .build();
        kvClient = client.getKVClient();
        lockClient = client.getLockClient();
        leaseClient = client.getLeaseClient();
        booksRepository = new BooksRepository(client, new ObjectMapper());

        // Проверка доступности серверов
        kvClient.get(AVAILABILITY_KEY).get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
    }

    @Test
    @Order(1)
    void optimisticLockingViewsCounter() throws Exception {
        // Создаём книгу
        kvClient.put(VIEWS_KEY, bytes("0"))
                .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

        final ExecutorService executor = Executors.newFixedThreadPool(WORKERS);
        final CountDownLatch start = new CountDownLatch(1);

        try {
            // OPTIMISTIC LOCK - Метод incrementViews не захватывает блокировку заранее: он сравнивает modRevision ключа
            // во время записи и повторяет операцию при обнаружении конфликта
            final List<Future<Long>> increments = IntStream.range(0, INCREMENTS)
                    .mapToObj(ignored -> executor.submit(() -> {
                        start.await();
                        return booksRepository.incrementViews(TEST_BOOK_ID)
                                .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
                    }))
                    .toList();

            // Запуск машины судного дня
            start.countDown();

            final Set<Long> returnedValues = new HashSet<>();
            for (Future<Long> increment : increments) {
                returnedValues.add(increment.get(TIMEOUT.toSeconds(), TimeUnit.SECONDS));
            }

            // Читаем
            final long storedViews = Long.parseLong(
                    kvClient.get(VIEWS_KEY)
                    .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                    .getKvs()
                    .get(0)
                    .getValue()
                    .toString(UTF_8)
            );
            final Set<Long> expectedValues = IntStream.rangeClosed(1, INCREMENTS)
                    .mapToObj(value -> (long) value)
                    .collect(Collectors.toSet());

            // Проверяем на потери и на то, что каждая операция получила соответствующий результат
            assertEquals(INCREMENTS, storedViews);
            assertEquals(expectedValues, returnedValues);

            System.out.printf(
                    "optimistic-locking: operations=%d, workers=%d, final-value=%d, lost-updates=%d%n",
                    INCREMENTS,
                    WORKERS,
                    storedViews,
                    INCREMENTS - storedViews
            );
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    @Order(2)
    void pessimisticLockingBookBorrowing() throws Exception {
        // Создаём книгу
        kvClient.put(AVAILABILITY_KEY, bytes("true"))
                .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

        final ExecutorService executor = Executors.newFixedThreadPool(2);
        final CountDownLatch workersReady = new CountDownLatch(2);
        final CountDownLatch start = new CountDownLatch(1);
        final AtomicInteger activeCriticalSections = new AtomicInteger();
        final AtomicInteger maximumCriticalSections = new AtomicInteger();

        try {
            // Перед чтением доступности каждый клиент обязан получить lock
            final Future<Boolean> firstBorrow = executor.submit(() -> borrowWithLock(
                    workersReady,
                    start,
                    activeCriticalSections,
                    maximumCriticalSections
            ));
            final Future<Boolean> secondBorrow = executor.submit(() -> borrowWithLock(
                    workersReady,
                    start,
                    activeCriticalSections,
                    maximumCriticalSections
            ));

            // Запуск по готовности
            assertTrue(workersReady.await(TIMEOUT.toSeconds(), TimeUnit.SECONDS));
            start.countDown();

            final long successfulBorrows = List.of(
                            firstBorrow.get(TIMEOUT.toSeconds(), TimeUnit.SECONDS),
                            secondBorrow.get(TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                    ).stream()
                    .filter(Boolean::booleanValue)
                    .count();

            // Проверяем результат и фактическую степень параллельности
            assertEquals(1L, successfulBorrows);
            assertFalse(
                    Boolean.parseBoolean(
                            kvClient.get(AVAILABILITY_KEY)
                        .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                        .getKvs()
                        .get(0)
                        .getValue()
                        .toString(UTF_8)
                    )
            );
            assertEquals(1, maximumCriticalSections.get());

            System.out.printf(
                    "pessimistic-locking: attempts=2, successful=%d, max-critical-sections=%d%n",
                    successfulBorrows,
                    maximumCriticalSections.get()
            );
        } finally {
            executor.shutdownNow();
        }
    }

    private boolean borrowWithLock(
            CountDownLatch workersReady,
            CountDownLatch start,
            AtomicInteger activeCriticalSections,
            AtomicInteger maximumCriticalSections
    ) throws Exception {
        workersReady.countDown();
        start.await();

        // Гарантия освобождения через 10 секунд
        final long leaseId = leaseClient.grant(10)
                .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                .getID();
        ByteSequence lockKey = null;

        try {
            // В отличие от optimistic locking, второй клиент ожидает
            lockKey = lockClient.lock(BOOK_LOCK, leaseId)
                    .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                    .getKey();

            final int currentCriticalSections = activeCriticalSections.incrementAndGet();
            maximumCriticalSections.accumulateAndGet(currentCriticalSections, Math::max);

            try {
                if (!Boolean.parseBoolean(
                        kvClient.get(AVAILABILITY_KEY)
                        .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS)
                        .getKvs()
                        .get(0)
                        .getValue()
                        .toString(UTF_8)
                )) {
                    return false;
                }

                // Задержка-костыль
                Thread.sleep(100);
                kvClient.put(AVAILABILITY_KEY, bytes("false"))
                        .get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);

                return true;
            } finally {
                activeCriticalSections.decrementAndGet();
            }
        } finally {
            if (lockKey != null) {
                lockClient.unlock(lockKey).get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
            }
            leaseClient.revoke(leaseId).get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        }
    }

    @AfterEach
    void removeTestData() throws Exception {
        kvClient.delete(AVAILABILITY_KEY).get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
        kvClient.delete(VIEWS_KEY).get(TIMEOUT.toSeconds(), TimeUnit.SECONDS);
    }

    @AfterAll
    static void closeClient() {
        if (client != null) {
            client.close();
        }
    }

    private static ByteSequence bytes(String value) {
        return ByteSequence.from(value, UTF_8);
    }
}
