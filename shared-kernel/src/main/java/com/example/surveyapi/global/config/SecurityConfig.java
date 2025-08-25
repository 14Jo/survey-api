package com.example.surveyapi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.surveyapi.global.auth.jwt.JwtAccessDeniedHandler;
import com.example.surveyapi.global.auth.jwt.JwtAuthenticationEntryPoint;
import com.example.surveyapi.global.auth.jwt.JwtFilter;
import com.example.surveyapi.global.auth.jwt.JwtUtil;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtUtil jwtUtil;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final RedisTemplate<String, String> redisTemplate;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.exceptionHandling(exceptions -> exceptions
				.authenticationEntryPoint(jwtAuthenticationEntryPoint)
				.accessDeniedHandler(jwtAccessDeniedHandler))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/auth/signup", "/api/auth/login").permitAll()
				.requestMatchers("/api/surveys/participations/count").permitAll()
				.requestMatchers("/api/auth/kakao/login").permitAll()
				.requestMatchers("/api/auth/naver/login").permitAll()
				.requestMatchers("/api/auth/google/login").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/actuator/**").permitAll()
				.anyRequest().authenticated())
			.addFilterBefore(new JwtFilter(jwtUtil, redisTemplate), UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
