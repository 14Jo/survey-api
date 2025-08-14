package com.example.surveyapi.domain.survey.infra.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.survey.application.event.SurveyEventPublisherPort;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyEventPublisher implements SurveyEventPublisherPort {

	private final RabbitTemplate rabbitTemplate;

	public void publish(SurveyEvent event, EventCode key) {
		if (key.equals(EventCode.SURVEY_ACTIVATED)) {
			rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, RabbitConst.ROUTING_KEY_SURVEY_ACTIVE, event);
		}
	}
}
