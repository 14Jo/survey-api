package com.example.surveyapi.global.config.client.participation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import com.example.surveyapi.domain.statistic.infra.external.participation.ParticipationApiClient;

@Configuration
public class ParticipationApiClientConfig {

	@Bean
	public ParticipationApiClient participationApiClient(RestClient restClient) {
		return HttpServiceProxyFactory
			.builderFor(RestClientAdapter.create(restClient))
			.build()
			.createClient(ParticipationApiClient.class);
	}
}
