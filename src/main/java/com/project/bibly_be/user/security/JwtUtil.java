package com.project.bibly_be.user.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

    public JwtUtil(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    @PostConstruct
    public void init() {
        System.out.println("🔹 JWT Secret Key: " + key);
    }

    //  JWT 생성
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    // JWT 검증 메서드 (추가)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            System.out.println("DEBUG: Token is valid.");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("DEBUG: JWT expired: " + e.getMessage());
            return false;
        } catch (JwtException | IllegalArgumentException e) {
            System.out.println("DEBUG: JWT validation error: " + e.getMessage());
            return false;
        }
    }

    // JWT에서 이메일 추출
    public String validateAndGetEmail(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException | IllegalArgumentException e) {
            return null;
        }
    }
}
