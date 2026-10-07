package org.repository;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.options.PutOption;
import org.models.GenreFilterSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static java.nio.charset.StandardCharsets.UTF_8;

@Repository
public class SettingsCacheRepository {
    private static final String SETTINGS_CACHE_KEY_PREFIX = "/cache/user-settings/";

    private final KV kvClient;
    private final Lease leaseClient;
    private final ObjectMapper objectMapper;
    private final long cacheTtlSeconds;

    public SettingsCacheRepository(
            Client client,
            ObjectMapper objectMapper,
            @Value("${settings-service.cache-ttl-seconds}") long cacheTtlSeconds
    ) {
        this.kvClient = client.getKVClient();
        this.leaseClient = client.getLeaseClient();
        this.objectMapper = objectMapper;
        this.cacheTtlSeconds = cacheTtlSeconds;
    }

    public CompletableFuture<Optional<GenreFilterSettings>> getByUsername(String username) {
        final ByteSequence key = key(username);

        return kvClient.get(key).thenApply(response -> {
            if (response.getKvs().isEmpty()) {
                return Optional.empty();
            }

            final String settingsJson = response.getKvs().get(0).getValue().toString(UTF_8);
            return Optional.of(deserialize(settingsJson));
        });
    }

    public CompletableFuture<Void> put(String username, GenreFilterSettings settings) {
        final ByteSequence key = key(username);
        final ByteSequence value = serialize(settings);

        return leaseClient.grant(cacheTtlSeconds).thenCompose(lease -> kvClient.put(
                        key,
                        value,
                        PutOption.builder().withLeaseId(lease.getID()).build()
                ))
                .thenApply(ignored -> null);
    }

    private ByteSequence key(String username) {
        return ByteSequence.from(SETTINGS_CACHE_KEY_PREFIX + username, UTF_8);
    }

    private ByteSequence serialize(GenreFilterSettings settings) {
        try {
            return ByteSequence.from(objectMapper.writeValueAsString(settings), UTF_8);
        } catch (JacksonException exception) {
            throw new CompletionException("Failed to serialize settings", exception);
        }
    }

    private GenreFilterSettings deserialize(String settingsJson) {
        try {
            return objectMapper.readValue(settingsJson, GenreFilterSettings.class);
        } catch (JacksonException exception) {
            throw new CompletionException("Failed to deserialize settings", exception);
        }
    }
}
