package com.example.surveyapi.global.config;

import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
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
	public CustomExchange customExchange() {
		return new CustomExchange(
			RabbitConst.DELAYED_EXCHANGE_NAME,
			"x-delayed-message",
			true,
			false,
			Map.of("x-delayed-type", "topic")
		);
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
	public Binding bindingUserWithdrawToProjectQueue(Queue queueProject, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueProject)
			.to(exchange)
			.with(RabbitConst.ROUTING_KEY_USER_WITHDRAW);
	}

	@Bean
	public Binding bindingProject(Queue queueProject, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueProject)
			.to(exchange)
			.with("project.*");
	}

	@Bean
	public Binding bindingSurveyFromProjectClosed(Queue queueSurvey, TopicExchange exchange) {
		return BindingBuilder
			.bind(queueSurvey)
			.to(exchange)
			.with(RabbitConst.ROUTING_KEY_PROJECT_DELETED);
	}

	@Bean
	public Binding bindingSurveyStartDue(Queue queueSurvey, CustomExchange customExchange) {
		return BindingBuilder
			.bind(queueSurvey)
			.to(customExchange)
			.with(RabbitConst.ROUTING_KEY_SURVEY_START_DUE)
			.noargs();
	}

	@Bean
	public Binding bindingSurveyEndDue(Queue queueSurvey, CustomExchange customExchange) {
		return BindingBuilder
			.bind(queueSurvey)
			.to(customExchange)
			.with(RabbitConst.ROUTING_KEY_SURVEY_END_DUE)
			.noargs();
	}
}
