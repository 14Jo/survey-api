package com.example.surveyapi.global.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.surveyapi.global.client.UserApiClient;

@Configuration
public class UserApiClientConfig {

	@Bean
	public UserApiClient userApiClient(RestClient restClient) {
		return HttpServiceProxyFactory
			.builderFor(RestClientAdapter.create(restClient))
			.build()
			.createClient(UserApiClient.class);
	}
}