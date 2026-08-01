package com.bank.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

/**
 * 📍 Concept: "Security Deep-Dive" + "Spring Boot Backend" (JWT section) notebooks
 *
 * Ye class Gateway ko bina Auth server call kiye, locally JWT verify karne
 * deti hai — signature check karke turant pata chal jaata hai token valid
 * hai ya nahi, koi extra network hop nahi lagta.
 */
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${banking.security.jwt-secret}") String secret) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public Claims validateAndExtractClaims(String token) {
        // Agar signature invalid ya token expired ho, ye method exception throw karega
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken(String username, String role) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new java.util.Date(now))
                .setExpiration(new java.util.Date(now + 3600_000)) // 1 hour expiry
                .signWith(secretKey)
                .compact();
    }
}
