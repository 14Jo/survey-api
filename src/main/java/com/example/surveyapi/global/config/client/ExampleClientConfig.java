package com.example.surveyapi.global.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class ExampleClientConfig {

	@Bean
	public ExampleApiClient exampleApiClient() {
		RestClient restClient = RestClient.builder()
			.baseUrl("https://localhost:8080/")
			.build();

		HttpServiceProxyFactory factory = HttpServiceProxyFactory
			.builderFor(RestClientAdapter.create(restClient))
			.build();

		return factory.createClient(ExampleApiClient.class);
	}
}
