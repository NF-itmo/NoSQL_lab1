package org;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;


@ComponentScan("org.filter")
@ComponentScan("org.jwt")
@EnableConfigurationProperties(JwtProperties.class)
public class JwtFilterConfig {
    private final JwtProperties props;

    public JwtFilterConfig(JwtProperties props) {
        this.props = props;
    }

    @Bean
    public Algorithm jwtAlgorithm() {
        return Algorithm.HMAC256(props.getSecret());
    }
}
