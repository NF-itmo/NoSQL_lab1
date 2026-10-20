package org.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

@Repository
public class BooksViewsRepository {
    private static final String BOOKS_VIEWS_KEY_PREFIX = "/books/views/";

    private final KV kvClient;

    public BooksViewsRepository(Client client) {
        this.kvClient = client.getKVClient();
    }

    public CompletableFuture<Long> incrementViews(Integer bookId) {
        final ByteSequence viewsKey = ByteSequence.from(
                BOOKS_VIEWS_KEY_PREFIX + bookId,
                UTF_8
        );

        return incrementViews(viewsKey);
    }

    private CompletableFuture<Long> incrementViews(ByteSequence viewsKey) {
        return kvClient.get(viewsKey).thenCompose(response -> {
            final boolean counterExists = !response.getKvs().isEmpty();
            final long views = counterExists ? Long.parseLong(response.getKvs().get(0).getValue().toString(UTF_8)) : 0L;
            final long nextViews = views + 1;
            final Cmp counterDidNotChange = counterExists
                    ? new Cmp(
                    viewsKey,
                    Cmp.Op.EQUAL,
                    CmpTarget.modRevision(response.getKvs().get(0).getModRevision())
            )
                    : new Cmp(viewsKey, Cmp.Op.EQUAL, CmpTarget.version(0));

            return kvClient.txn()
                    .If(counterDidNotChange)
                    .Then(Op.put(
                            viewsKey,
                            ByteSequence.from(Long.toString(nextViews), UTF_8),
                            PutOption.DEFAULT
                    ))
                    .commit()
                    .thenCompose(transaction -> transaction.isSucceeded()
                            ? CompletableFuture.completedFuture(nextViews)
                            : incrementViews(viewsKey));
        });
    }
}
