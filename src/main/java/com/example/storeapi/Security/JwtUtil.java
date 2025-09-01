package com.example.storeapi.Security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private Key useForSigning() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String userName, String role) {
        Date dateNow = new Date();
        Date dateExp = new Date(dateNow.getTime() + expirationMs);

        return Jwts.builder()
                .setSubject(userName)
                .claim("role", role)
                .setIssuedAt(dateNow)
                .setExpiration(dateExp)
                .signWith(useForSigning(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(useForSigning())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
