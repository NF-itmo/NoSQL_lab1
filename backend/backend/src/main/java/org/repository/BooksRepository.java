package org.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.options.GetOption;
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
}
