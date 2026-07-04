package com.meraki.website.security;

import java.time.Instant;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

@Service
public class JwtService {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final String issuer;
    private final long expirationSeconds;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.expiration-seconds}") long expirationSeconds
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.issuer = issuer;
        this.expirationSeconds = expirationSeconds;
        this.verifier = JWT.require(algorithm).withIssuer(issuer).build();
    }

    public String createToken(String userId, String role) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationSeconds);
        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(exp))
                .withClaim("x-user", userId)
                .withClaim("x-role", role)
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }
}

