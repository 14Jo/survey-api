package com.example.surveyapi.global.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.surveyapi.global.client.ProjectApiClient;

@Configuration
public class ProjectApiClientConfig {

	@Bean
	public RestClient projectRestClient() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(1_000);
		factory.setReadTimeout(1_000);

		return RestClient.builder()
			.baseUrl("http://localhost:8080")
			.defaultHeader("Accept", "application/json")
			.requestFactory(factory)
			.build();
	}

	@Bean
	public ProjectApiClient projectApiClient(RestClient projectRestClient) {
		return HttpServiceProxyFactory
			.builderFor(RestClientAdapter.create(projectRestClient))
			.build()
			.createClient(ProjectApiClient.class);
	}
}