package com.example.surveyapi.global.config.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.surveyapi.global.client.ShareApiClient;

@Configuration
public class ShareApiClientConfig {

	@Bean
	public ShareApiClient shareApiClient(RestClient restClient) {
		return HttpServiceProxyFactory
			.builderFor(RestClientAdapter.create(restClient))
			.build()
			.createClient(ShareApiClient.class);
	}
}