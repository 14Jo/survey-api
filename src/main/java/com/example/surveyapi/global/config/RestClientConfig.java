package com.example.surveyapi.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	//TODO : base url 환경 변수 처리하기
	@Bean
	public RestClient restClient() {
		return RestClient.builder()
			.baseUrl("http://localhost:8080")
			.build();
	}
}
