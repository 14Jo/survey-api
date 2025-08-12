package com.example.surveyapi.domain.survey.infra.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EventPublisher {

	private final RabbitTemplate rabbitTemplate;

	public void publishEvent(SurveyEvent event, EventCode key) {
		String routingKey = RabbitConst.ROUTING_KEY.replace("#", key.name());
		rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, routingKey, event);
	}
}
