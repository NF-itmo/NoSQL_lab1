package org;

import org.config.EtcdClientConfig;
import org.config.ObjectMapperConfig;
import org.models.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.boot.persistence.autoconfigure.EntityScan;


@SpringBootApplication(scanBasePackages = {
        "org.config",
        "org.controllers",
        "org.repository",
        "org.services"
})
@EntityScan(basePackageClasses = User.class)
@Import({JwtFilterConfig.class, ObjectMapperConfig.class, EtcdClientConfig.class})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}