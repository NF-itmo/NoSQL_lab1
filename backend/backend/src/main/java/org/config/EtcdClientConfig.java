package org.config;

import io.etcd.jetcd.Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EtcdClientConfig {
    @Bean(destroyMethod = "close")
    public Client etcdClient(@Value("${etcd.endpoint}") String endpoint) {
        return Client.builder().endpoints(endpoint).build();
    }
}
