package com.example.surveyapi.global.config;

import static org.springframework.amqp.core.AcknowledgeMode.*;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.core.Queue;

import com.example.surveyapi.global.constant.RabbitConst;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

	private final ConnectionFactory connectionFactory;

	@Bean
	public SimpleRabbitListenerContainerFactory defaultListenerContainerFactory() {

		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);

		factory.setConsumerBatchEnabled(false);
		factory.setMessageConverter(jsonMessageConverter());

		// 동시 처리 설정
		factory.setConcurrentConsumers(3);
		factory.setMaxConcurrentConsumers(5);

		return factory;
	}

	// JSON 메시지 변환기
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
