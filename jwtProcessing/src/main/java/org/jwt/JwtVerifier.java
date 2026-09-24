package org.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.JwtProperties;
import org.springframework.stereotype.Component;

@Component
public class JwtVerifier {
    private final Algorithm jwtAlgorithm;
    private final String expectedIssuer;

    public JwtVerifier(
            Algorithm jwtAlgorithm,
            JwtProperties props
    ) {
        this.jwtAlgorithm = jwtAlgorithm;
        this.expectedIssuer = props.getIssuer();
    }

    public DecodedJWT verify(String token) throws JWTVerificationException {
        return JWT.require(this.jwtAlgorithm)
                .withIssuer(this.expectedIssuer)
                .build()
                .verify(token);
    }
}
