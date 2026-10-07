package org.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.LeaseOption;
import io.etcd.jetcd.options.PutOption;
import lombok.extern.slf4j.Slf4j;
import org.models.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Repository
@Slf4j
public class OrdersRepository {
    private static final String ORDERS_KEY_PREFIX = "/orders/";
    private final KV kvClient;
    private final Lease leaseClient;
    private final ObjectMapper objectMapper;
    private final long orderTtlSeconds;

    public OrdersRepository(
            Client client,
            ObjectMapper objectMapper,
            @Value("${orders-service.order-ttl-seconds}") final long orderTtlSeconds
    ) {
        kvClient = client.getKVClient();
        leaseClient = client.getLeaseClient();
        this.objectMapper = objectMapper;
        this.orderTtlSeconds = orderTtlSeconds;
    }

    public CompletableFuture<Optional<Order>> getById(Integer orderId) {
        final ByteSequence key = ByteSequence.from(ORDERS_KEY_PREFIX + orderId, UTF_8);

        return kvClient.get(key).thenCompose(response -> {
            if (response.getKvs().isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }

            return mapOrder(response.getKvs().get(0)).thenApply(Optional::of);
        });
    }

    public CompletableFuture<List<Order>> getAll() {
        final ByteSequence prefix = ByteSequence.from(ORDERS_KEY_PREFIX, UTF_8);
        final GetOption option = GetOption.builder().isPrefix(true).build();

        return kvClient.get(prefix, option).thenCompose(response -> {
            final List<CompletableFuture<Order>> orderFutures = response.getKvs().stream()
                    .map(this::mapOrder)
                    .toList();

            final CompletableFuture<?>[] futures = orderFutures.toArray(CompletableFuture[]::new);

            return CompletableFuture.allOf(futures)
                    .thenApply(ignored -> orderFutures.stream()
                            .map(CompletableFuture::join)
                            .toList());
        });
    }

    public CompletableFuture<Void> deleteById(Integer orderId) {
        final ByteSequence key = ByteSequence.from(ORDERS_KEY_PREFIX + orderId, UTF_8);

        return kvClient.delete(key).thenApply(response -> null);
    }

    public CompletableFuture<Order> createOrderByBookId(Integer bookId) {
        final ByteSequence key = ByteSequence.from(ORDERS_KEY_PREFIX + bookId, UTF_8);
        final ByteSequence value = serializeOrder(bookId);

        return leaseClient.grant(orderTtlSeconds).thenCompose(leaseGrantResponse ->
                kvClient.put(
                                key,
                                value,
                                PutOption.builder()
                                        .withLeaseId(leaseGrantResponse.getID())
                                        .build()
                        )
                        .thenApply(putResponse -> Order.builder()
                                .id(bookId)
                                .bookId(bookId)
                                .expiresAt(Instant.now().plusSeconds(orderTtlSeconds))
                                .build())
        );
    }

    private ByteSequence serializeOrder(Integer bookId) {
        try {
            final Order order = Order.builder()
                    .id(bookId)
                    .bookId(bookId)
                    .build();

            return ByteSequence.from(objectMapper.writeValueAsString(order), UTF_8);
        } catch (JacksonException exception) {
            log.error("Failed to serialize order", exception);
            throw new CompletionException("Failed to serialize order", exception);
        }
    }

    private CompletableFuture<Order> mapOrder(KeyValue keyValue) {
        final String orderJson = keyValue.getValue().toString(UTF_8);
        final Integer orderId = Integer.parseInt(keyValue.getKey()
                .toString(UTF_8)
                .substring(ORDERS_KEY_PREFIX.length()));

        return leaseClient.timeToLive(keyValue.getLease(), LeaseOption.DEFAULT)
                .thenApply(lease -> deserializeOrder(orderId, orderJson, lease.getTTL()));
    }

    private Order deserializeOrder(Integer orderId, String orderJson, long remainingTtlSeconds) {
        try {
            final Order order = objectMapper.readValue(orderJson, Order.class);
            order.setId(orderId);
            order.setExpiresAt(Instant.now().plusSeconds(remainingTtlSeconds));

            return order;
        } catch (JacksonException exception) {
            log.error("Failed to deserialize order", exception);
            throw new CompletionException("Failed to deserialize order", exception);
        }
    }

}
