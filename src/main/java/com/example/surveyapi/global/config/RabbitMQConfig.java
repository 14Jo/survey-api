package com.example.surveyapi.global.config;

import static org.springframework.amqp.core.AcknowledgeMode.*;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import com.example.surveyapi.global.constant.RabbitConst;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

	private final ConnectionFactory connectionFactory;

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(RabbitConst.EXCHANGE_NAME);
	}

	@Bean
	public Queue queue() {
		return new Queue(RabbitConst.QUEUE_NAME, true);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {

		return BindingBuilder
			.bind(queue)
			.to(exchange)
			.with(RabbitConst.ROUTING_KEY);
	}

	@Bean TopicExchange participationExchange() {
		return new TopicExchange(RabbitConst.PARTICIPATION_EXCHANGE_NAME);
	}

	@Bean
	public Queue participationQueue() {
		return new Queue(RabbitConst.PARTICIPATION_QUEUE_NAME, true);
	}

	@Bean
	public Binding participationBinding(Queue participationQueue, TopicExchange participationExchange) {

		return BindingBuilder
			.bind(participationQueue)
			.to(participationExchange)
			.with(RabbitConst.PARTICIPATION_ROUTING_KEY);
	}

	@Bean
	public SimpleRabbitListenerContainerFactory batchListenerContainerFactory() {

		SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);

		factory.setConsumerBatchEnabled(true);
		factory.setBatchSize(100);
		factory.setBatchReceiveTimeout(5000L);

		factory.setAcknowledgeMode(MANUAL);

		factory.setConcurrentConsumers(1);
		factory.setMaxConcurrentConsumers(1);

		factory.setMessageConverter(jsonMessageConverter());

		factory.setBatchListener(true);

		return factory;
	}

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

	// 🔄 JSON 메시지 변환기
	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}
}
