package com.example.surveyapi.global.config.client;

import com.example.surveyapi.global.client.StatisticApiClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class StatisticApiClientConfig {

    @Bean
    public StatisticApiClient statisticApiClient(RestClient restClient) {
        RestClientAdapter adapter = RestClientAdapter.create(restClient);
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();
        return factory.createClient(StatisticApiClient.class);
    }
}