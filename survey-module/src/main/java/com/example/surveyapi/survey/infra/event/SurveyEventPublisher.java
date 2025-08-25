package com.example.surveyapi.survey.infra.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.surveyapi.survey.application.event.SurveyEventPublisherPort;
import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.survey.SurveyEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyEventPublisher implements SurveyEventPublisherPort {

	private final RabbitTemplate rabbitTemplate;

	@Override
	public void publish(SurveyEvent event, EventCode key) {
		if (key.equals(EventCode.SURVEY_ACTIVATED)) {
			rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, RabbitConst.ROUTING_KEY_SURVEY_ACTIVE, event);
		}
	}

	@Override
	public void publishDelayed(SurveyEvent event, String routingKey, long delayMs) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("x-delay", delayMs);
		rabbitTemplate.convertAndSend(RabbitConst.DELAYED_EXCHANGE_NAME, routingKey, event, message -> {
			message.getMessageProperties().getHeaders().putAll(headers);
			return message;
		});
	}
}
