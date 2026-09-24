package org.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class JwtProvider {
    private final JwtVerifier verifier;
    private final JwtBuilderFactory builderFactory;

    public JwtProvider(
            JwtVerifier verifier,
            JwtBuilderFactory builderFactory
    ) {
        this.verifier = verifier;
        this.builderFactory = builderFactory;
    }

    public String generateToken(String username) {
        return builderFactory.create()
                .addSubject(username)
                .build();
    }

    public String generateToken(
            String username,
            HashMap<String, Object> claims
    ) {
        var builder = builderFactory.create()
                .addSubject(username);

        if (claims != null) {
            claims.forEach(builder::addClaim);
        }

        return builder.build();
    }

    public boolean validateToken(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsername(String token) {
        try {
            return verifier.verify(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    public String getClaim(String token, String claimName) {
        try {
            return verifier.verify(token).getClaim(claimName).asString();
        } catch (Exception e) {
            return null;
        }
    }

    public DecodedJWT getDecodedJwt(String token) {
        try {
            return verifier.verify(token);
        } catch (Exception e) {
            return null;
        }
    }
}