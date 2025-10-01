package com.example.backend.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.token.expiration-time}")
    private long expirationTime;

    // 🔽🔽🔽 임시 토큰 유효시간 (10분) 🔽🔽🔽
    private final long temporaryTokenValidityInMillis = 10 * 60 * 1000L;

    private Key getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(Long userId) {
        return createToken(userId, expirationTime);
    }

    // ▼▼▼▼▼ 임시 토큰 생성 메소드 수정 ▼▼▼▼▼
    public String generateTemporaryToken(Long userId) {
        return createToken(userId, temporaryTokenValidityInMillis);
    }
    // ▲▲▲▲▲ 여기까지 수정 ▲▲▲▲▲

    private String createToken(Long userId, long validityInMillis) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + validityInMillis);

        return Jwts.builder()
                .setSubject(Long.toString(userId))
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }


    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}