package com.nova.yeobaek.global.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String ROLE_CLAIM = "role";

    private final SecretKey secretKey;
    private final Duration accessTokenExpiration;
    private final Duration refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.token.secret-key}") String secret,
            @Value("${jwt.token.expiration.access}") long accessExpiration,
            @Value("${jwt.token.expiration.refresh}") long refreshExpiration
    ) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw new IllegalArgumentException(
                    "JWT secret key must be at least 256 bits (32 bytes)"
            );
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = Duration.ofMillis(accessExpiration);
        this.refreshTokenExpiration = Duration.ofMillis(refreshExpiration);
    }

    public String createAccessToken(Long userId, String role) {
        return createToken(
                userId,
                Map.of(ROLE_CLAIM, role),
                accessTokenExpiration
        );
    }

    public String createRefreshToken(Long userId) {
        return createToken(
                userId,
                Map.of(),
                refreshTokenExpiration
        );
    }

    private String createToken(
            Long userId,
            Map<String, Object> claims,
            Duration expiration
    ) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration.toMillis());

        return Jwts.builder()
                .subject(String.valueOf(userId))
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    // 토큰 검증
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }

    public String getRole(String token) {
        return parseClaims(token).get(ROLE_CLAIM, String.class);
    }


    // 토큰 검증
    public void validateToken(String token) {
        parseClaims(token);
    }

    public long getRemainingExpirationMillis(String token) {
        Claims claims = parseClaims(token);
        long expirationTime = claims.getExpiration().getTime();
        long now = System.currentTimeMillis();
        return Math.max(expirationTime - now, 0);
    }

}
