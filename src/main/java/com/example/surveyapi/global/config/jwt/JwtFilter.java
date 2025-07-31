package com.example.surveyapi.global.config.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;


import com.example.surveyapi.domain.user.domain.user.enums.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);

        String blackListToken = "blackListToken" + token;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(blackListToken))){
            request.setAttribute("exceptionMessage", "로그아웃한 유저입니다.");
            throw new InsufficientAuthenticationException("로그아웃한 유저입니다.");
        }

        try{
            if (token != null && jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.extractClaims(token);

                Long userId = Long.parseLong(claims.getSubject());
                Role userRole = Role.valueOf(claims.get("userRole", String.class));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of(new SimpleGrantedAuthority("ROLE_" + userRole.name())));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }catch (JwtException e) {
            log.error("Jwt validation failed {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }catch (Exception e){
            log.error("Authentication error", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken (HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
