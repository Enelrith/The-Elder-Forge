package com.enelrith.theelderforge.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
@Slf4j
public class JwtService {
    @Value("${spring.security.jwt.secret}")
    private String secret;
    @Value("${spring.security.jwt.expiration-access-ms}")
    private long accessExpirationMs;
    @Value("${spring.security.jwt.expiration-refresh-ms}")
    private long refreshExpirationMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(UserDetails userDetails) {
        var token = generateToken(userDetails, accessExpirationMs);
        log.info("Generated access token for user {}", userDetails.getUsername());

        return token;
    }

    public String generateRefreshToken(UserDetails userDetails) {
        var token = generateToken(userDetails, refreshExpirationMs);
        log.info("Generated refresh token for user {}", userDetails.getUsername());

        return token;
    }

    private String generateToken(UserDetails userDetails, long expirationMs) {
        var now = new Date();
        return Jwts.builder()
                .issuer("theelderforge")
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(secretKey)
                .compact();
    }

    public String getEmailFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.warn("Failed to validate JWT: {}", e.getMessage());
        }
        return false;
    }
}
