package com.example.surveyapi.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.surveyapi.global.constant.RabbitConst;

@Configuration
public class RabbitMQBindingConfig {

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(RabbitConst.EXCHANGE_NAME);
	}

	@Bean
	public Queue queueUser() {
		return new Queue(RabbitConst.QUEUE_NAME_USER, true);
	}

	@Bean
	public Queue queueSurvey() {
		return new Queue(RabbitConst.QUEUE_NAME_SURVEY, true);
	}

	@Bean
	public Queue queueParticipation() {
		return new Queue(RabbitConst.QUEUE_NAME_PARTICIPATION, true);
	}

	@Bean
	public Queue queueShare() {
		return new Queue(RabbitConst.QUEUE_NAME_SHARE, true);
	}

	@Bean
	public Queue queueStatistic() {
		return new Queue(RabbitConst.QUEUE_NAME_STATISTIC, true);
	}

	@Bean
	public Queue queueProject() {
		return new Queue(RabbitConst.QUEUE_NAME_PROJECT, true);
	}

	@Bean
	public Binding bindingStatistic(Queue queueStatistic, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueStatistic)
			.to(exchange)
			.with(RabbitConst.ROUTING_KEY_SURVEY_ACTIVE);
	}

	@Bean
	public Binding bindingShare(Queue queueShare, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueShare)
			.to(exchange)
			.with(RabbitConst.ROUTING_KEY_SURVEY_ACTIVE);
	}

	@Bean
	public Binding bindingUser(Queue queueUser, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueUser)
			.to(exchange)
			.with(RabbitConst.ROUTING_KEY_SURVEY_ACTIVE);
	}

	@Bean
	public Binding bindingStatisticParticipation(Queue queueStatistic, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueStatistic)
			.to(exchange)
			.with("participation.*");
	}

	@Bean
	public Binding bindingUserParticipation(Queue queueUser, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueUser)
			.to(exchange)
			.with(RabbitConst.ROUTING_KEY_PARTICIPATION_CREATE);
	}

}
