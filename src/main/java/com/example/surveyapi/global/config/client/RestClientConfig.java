package com.example.surveyapi.global.config.client;

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

	@Bean
	public RestClient restClient(ClientHttpRequestFactory clientHttpRequestFactory) {
		return RestClient.builder()
			.baseUrl("http://localhost:8080")
			.requestFactory(clientHttpRequestFactory)
			.build();
	}

	@Bean
	public ClientHttpRequestFactory clientHttpRequestFactory(CloseableHttpClient httpClient) {
		return new HttpComponentsClientHttpRequestFactory(httpClient);
	}

	@Bean
	public CloseableHttpClient httpClient(PoolingHttpClientConnectionManager poolingHttpClientConnectionManager) {
		RequestConfig requestConfig = RequestConfig.custom()
			.setConnectionRequestTimeout(Timeout.ofSeconds(3))
			.setConnectTimeout(Timeout.ofSeconds(5))
			.setResponseTimeout(Timeout.ofSeconds(10))
			.build();

		return HttpClients.custom()
			.setConnectionManager(poolingHttpClientConnectionManager)
			.setDefaultRequestConfig(requestConfig)
			.build();
	}

	@Bean
	public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
		connectionManager.setMaxTotal(20);
		connectionManager.setDefaultMaxPerRoute(20);
		return connectionManager;
	}
}
