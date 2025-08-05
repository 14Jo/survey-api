package com.example.surveyapi.global.config;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

	/**
	 * RestClient 빈 생성 - 외부 API 호출용
	 */
	@Bean
	public RestClient restClient(ClientHttpRequestFactory clientHttpRequestFactory) {
		return RestClient.builder()
			.baseUrl("http://localhost:8080")
			.requestFactory(clientHttpRequestFactory)
			.build();
	}

	/**
	 * HTTP 클라이언트 요청 팩토리 생성
	 */
	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	/**
	 * HTTP 클라이언트 생성 - 타임아웃 및 커넥션 풀 설정
	 */
	@Bean
	public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
		RequestConfig requestConfig = RequestConfig.custom()
			.setConnectionRequestTimeout(Timeout.ofSeconds(5))  // 커넥션 요청 타임아웃 증가
			.setConnectTimeout(Timeout.ofSeconds(5))           // 연결 타임아웃
			.setResponseTimeout(Timeout.ofSeconds(30))         // 응답 타임아웃 증가 (외부 API 지연 고려)
			.build();

		return HttpClients.custom()
			.setConnectionManager(poolingHttpClientConnectionManager)
			.setDefaultRequestConfig(requestConfig)
			.build();
	}

	/**
	 * 커넥션 풀 매니저 생성 - m3.micro 환경 최적화 (1GB 메모리, 1 vCPU)
	 */
	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(10);
		connectionManager.setDefaultMaxPerRoute(3);
		return connectionManager;
	}
}
