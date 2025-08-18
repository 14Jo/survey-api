package com.example.surveyapi.global.config;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequestInterceptor;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;

@Configuration
public class ElasticsearchConfig {

	@Bean
	public ElasticsearchClient elasticsearchClient() {
		// RestClientBuilder 생성
		RestClientBuilder builder = RestClient.builder(new HttpHost("localhost", 9200))
			.setHttpClientConfigCallback(httpClientBuilder ->
				httpClientBuilder.addInterceptorLast(
					(HttpRequestInterceptor) (request, context) -> {
						System.out.println("HTTP Request: " + request.getRequestLine());
					}
				)
			);

		// Low-level RestClient 생성
		RestClient restClient = builder.build();

		// 고수준 ElasticsearchClient 생성
		return new ElasticsearchClient(
			new RestClientTransport(restClient, new JacksonJsonpMapper())
		);
	}
}