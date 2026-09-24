package org.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.JwtProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtBuilder {
    private final Algorithm algorithm;
    private final JwtProperties props;

    private String subject;
    private final Map<String, Object> claims = new HashMap<>();
    private Long expirationOverride;

    public JwtBuilder(Algorithm algorithm, JwtProperties props) {
        this.algorithm = algorithm;
        this.props = props;
    }

    public JwtBuilder addSubject(String subject) {
        this.subject = subject;
        return this;
    }

    public JwtBuilder addClaim(String key, Object value) {
        claims.put(key, value);
        return this;
    }

    public String build() {
        long exp = (expirationOverride != null)
                ? expirationOverride
                : props.getExpiration();

        var builder = JWT.create()
                .withIssuer(props.getIssuer())
                .withSubject(subject)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + exp * 1000));

        claims.forEach((k, v) -> {
            if (v instanceof String s) builder.withClaim(k, s);
            else if (v instanceof Integer i) builder.withClaim(k, i);
            else if (v instanceof Long l) builder.withClaim(k, l);
            else if (v instanceof Boolean b) builder.withClaim(k, b);
            else builder.withClaim(k, v.toString());
        });

        return builder.sign(algorithm);
    }

    public void reset() {
        subject = null;
        claims.clear();
        expirationOverride = null;
    }
}