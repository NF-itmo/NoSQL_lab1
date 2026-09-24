package org.jwt;

import com.auth0.jwt.algorithms.Algorithm;
import org.JwtProperties;
import org.springframework.stereotype.Component;

@Component
public class JwtBuilderFactory {
    private final Algorithm algorithm;
    private final JwtProperties props;

    public JwtBuilderFactory(Algorithm algorithm, JwtProperties props) {
        this.algorithm = algorithm;
        this.props = props;
    }

    public JwtBuilder create() {
        return new JwtBuilder(algorithm, props);
    }
}