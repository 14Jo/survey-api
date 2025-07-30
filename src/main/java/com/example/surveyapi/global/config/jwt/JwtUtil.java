package com.example.surveyapi.global.config.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.example.surveyapi.domain.user.domain.user.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${SECRET_KEY}") String secretKey) {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L;

    public String createToken(Long userId, Role userRole) {
        Date date = new Date();

        return BEARER_PREFIX +
            Jwts.builder()
                .subject(String.valueOf(userId))
                .claim("userRole", userRole)
                .expiration(new Date(date.getTime() + TOKEN_TIME))
                .issuedAt(date)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        }catch (SecurityException | MalformedJwtException e) {
            log.warn("Invalid JWT token: {}", e.getMessage());
            throw new JwtException("잘못된 형식의 토큰입니다");
        }catch (ExpiredJwtException e) {
            log.warn("Expired JWT token: {}", e.getMessage());
            throw new JwtException("만료된 토큰입니다");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token: {}", e.getMessage());
            throw new JwtException("지원하지 않는 토큰입니다");
        } catch (IllegalArgumentException e) {
            log.warn("JWT claims string is empty: {}", e.getMessage());
            throw new JwtException("토큰 정보가 비어있습니다");
        }
    }

    public String subStringToken(String token) {
        if (StringUtils.hasText(token) && (token.startsWith(BEARER_PREFIX))) {
            return token.substring(7);
        }
        throw new RuntimeException("NOT FOUND TOKEN");
    }

    public Claims extractToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }


}
