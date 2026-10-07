package org.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.PutOption;
import org.models.Book;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Repository
public class BooksRepository {
    private static final String BOOKS_DATA_KEY_PREFIX = "/books/data/";
    private static final String BOOKS_AVAILABILITY_KEY_PREFIX = "/books/availability/";
    private static final ByteSequence AVAILABLE_VALUE = ByteSequence.from("true", UTF_8);

    private final KV kvClient;
    private final ObjectMapper objectMapper;

    public BooksRepository(Client client, ObjectMapper objectMapper) {
        this.kvClient = client.getKVClient();
        this.objectMapper = objectMapper;
    }

    public CompletableFuture<List<Book>> getAll() {
        final ByteSequence prefix = ByteSequence.from(BOOKS_DATA_KEY_PREFIX, UTF_8);
        final GetOption option = GetOption.builder().isPrefix(true).build();

        return kvClient.get(prefix, option).thenCompose(response -> {
            final List<CompletableFuture<Book>> bookFutures = response.getKvs().stream()
                    .map(this::mapBook)
                    .toList();

            return CompletableFuture.allOf(bookFutures.toArray(CompletableFuture[]::new))
                    .thenApply(ignored -> bookFutures.stream()
                            .map(CompletableFuture::join)
                            .toList());
        });
    }

    public CompletableFuture<Optional<Book>> getById(Integer bookId) {
        final ByteSequence key = ByteSequence.from(BOOKS_DATA_KEY_PREFIX + bookId, UTF_8);

        return kvClient.get(key).thenCompose(response -> {
            if (response.getKvs().isEmpty()) {
                return CompletableFuture.completedFuture(Optional.empty());
            }

            return mapBook(response.getKvs().get(0)).thenApply(Optional::of);
        });
    }

    public CompletableFuture<Boolean> create(Book book) {
        final ByteSequence dataKey = ByteSequence.from(
                BOOKS_DATA_KEY_PREFIX + book.getId(),
                UTF_8
        );
        final ByteSequence availabilityKey = ByteSequence.from(
                BOOKS_AVAILABILITY_KEY_PREFIX + book.getId(),
                UTF_8
        );
        final ByteSequence value = serializeBook(book);

        return kvClient.txn()
                .If(new Cmp(dataKey, Cmp.Op.EQUAL, CmpTarget.version(0)))
                .Then(
                        Op.put(dataKey, value, PutOption.DEFAULT),
                        Op.put(availabilityKey, AVAILABLE_VALUE, PutOption.DEFAULT)
                )
                .commit()
                .thenApply(TxnResponse::isSucceeded);
    }

    public CompletableFuture<Void> deleteById(Integer bookId) {
        final ByteSequence dataKey = ByteSequence.from(
                BOOKS_DATA_KEY_PREFIX + bookId,
                UTF_8
        );
        final ByteSequence availabilityKey = ByteSequence.from(
                BOOKS_AVAILABILITY_KEY_PREFIX + bookId,
                UTF_8
        );

        return kvClient.txn()
                .Then(
                        Op.delete(dataKey, DeleteOption.DEFAULT),
                        Op.delete(availabilityKey, DeleteOption.DEFAULT)
                )
                .commit()
                .thenApply(response -> null);
    }

    public CompletableFuture<Boolean> release(Integer bookId) {
        final ByteSequence dataKey = ByteSequence.from(
                BOOKS_DATA_KEY_PREFIX + bookId,
                UTF_8
        );
        final ByteSequence availabilityKey = ByteSequence.from(
                BOOKS_AVAILABILITY_KEY_PREFIX + bookId,
                UTF_8
        );

        return kvClient.txn()
                .If(new Cmp(dataKey, Cmp.Op.GREATER, CmpTarget.version(0)))
                .Then(Op.put(availabilityKey, AVAILABLE_VALUE, PutOption.DEFAULT))
                .commit()
                .thenApply(TxnResponse::isSucceeded);
    }

    private CompletableFuture<Book> mapBook(KeyValue keyValue) {
        final Integer bookId = Integer.parseInt(keyValue.getKey()
                .toString(UTF_8)
                .substring(BOOKS_DATA_KEY_PREFIX.length()));
        final String bookJson = keyValue.getValue().toString(UTF_8);
        final ByteSequence availabilityKey = ByteSequence.from(
                BOOKS_AVAILABILITY_KEY_PREFIX + bookId,
                UTF_8
        );

        return kvClient.get(availabilityKey).thenApply(response -> {
            final Book book = deserializeBook(bookJson);
            book.setId(bookId);
            book.setAvailable(!response.getKvs().isEmpty()
                    && Boolean.parseBoolean(response.getKvs().get(0).getValue().toString(UTF_8)));

            return book;
        });
    }

    private Book deserializeBook(String bookJson) {
        try {
            return objectMapper.readValue(bookJson, Book.class);
        } catch (JacksonException exception) {
            throw new CompletionException("Failed to deserialize book", exception);
        }
    }

    private ByteSequence serializeBook(Book book) {
        try {
            return ByteSequence.from(objectMapper.writeValueAsString(book), UTF_8);
        } catch (JacksonException exception) {
            throw new CompletionException("Failed to serialize book", exception);
        }
    }
}
