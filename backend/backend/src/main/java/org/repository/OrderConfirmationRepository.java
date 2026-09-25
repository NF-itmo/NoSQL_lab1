package org.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.kv.TxnResponse;
import io.etcd.jetcd.op.Cmp;
import io.etcd.jetcd.op.CmpTarget;
import io.etcd.jetcd.op.Op;
import io.etcd.jetcd.options.DeleteOption;
import io.etcd.jetcd.options.PutOption;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

@Repository
public class OrderConfirmationRepository {
    private static final String ORDERS_KEY_PREFIX = "/orders/";
    private static final String BOOKS_AVAILABILITY_KEY_PREFIX = "/books/availability/";
    private static final ByteSequence AVAILABLE_VALUE = ByteSequence.from("true", UTF_8);
    private static final ByteSequence UNAVAILABLE_VALUE = ByteSequence.from("false", UTF_8);

    private final KV kvClient;

    public OrderConfirmationRepository(Client client) {
        this.kvClient = client.getKVClient();
    }

    public CompletableFuture<Boolean> confirm(Integer orderId, Integer bookId) {
        final ByteSequence orderKey = ByteSequence.from(ORDERS_KEY_PREFIX + orderId, UTF_8);
        final ByteSequence availabilityKey = ByteSequence.from(
                BOOKS_AVAILABILITY_KEY_PREFIX + bookId,
                UTF_8
        );

        return kvClient.txn()
                .If(
                        new Cmp(orderKey, Cmp.Op.GREATER, CmpTarget.version(0)),
                        new Cmp(availabilityKey, Cmp.Op.EQUAL, CmpTarget.value(AVAILABLE_VALUE))
                )
                .Then(
                        Op.put(availabilityKey, UNAVAILABLE_VALUE, PutOption.DEFAULT),
                        Op.delete(orderKey, DeleteOption.DEFAULT)
                )
                .commit()
                .thenApply(TxnResponse::isSucceeded);
    }
}
