package org;

import org.config.EtcdClientConfig;
import org.config.ObjectMapperConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;


@SpringBootApplication()
@Import({JwtFilterConfig.class, ObjectMapperConfig.class, EtcdClientConfig.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}