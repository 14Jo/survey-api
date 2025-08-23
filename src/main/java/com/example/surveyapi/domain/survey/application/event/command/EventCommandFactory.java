package com.example.surveyapi.domain.survey.application.event.command;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.event.SurveyEventPublisherPort;
import com.example.surveyapi.domain.survey.application.event.SurveyFallbackService;
import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.survey.SurveyEndDueEvent;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

/**
 * 이벤트 명령 생성 팩토리
 * 다양한 이벤트 타입에 따라 적절한 Command 객체들을 생성
 */
@Component
@RequiredArgsConstructor
public class EventCommandFactory {

	private final SurveyEventPublisherPort publisher;
	private final ObjectMapper objectMapper;

	public EventCommand createActivateEventCommand(ActivateEvent activateEvent) {
		return new PublishActivateEventCommand(publisher, objectMapper, activateEvent);
	}

	public EventCommand createDelayedEventCommand(
		Long surveyId,
		Long creatorId,
		String routingKey,
		LocalDateTime scheduledAt
	) {

		long delayMs = Duration.between(LocalDateTime.now(), scheduledAt).toMillis();

		if (RabbitConst.ROUTING_KEY_SURVEY_START_DUE.equals(routingKey)) {
			SurveyStartDueEvent event = new SurveyStartDueEvent(surveyId, creatorId, scheduledAt);
			return new PublishDelayedEventCommand(publisher, event, routingKey, delayMs);
		} else if (RabbitConst.ROUTING_KEY_SURVEY_END_DUE.equals(routingKey)) {
			SurveyEndDueEvent event = new SurveyEndDueEvent(surveyId, creatorId, scheduledAt);
			return new PublishDelayedEventCommand(publisher, event, routingKey, delayMs);
		}

		throw new IllegalArgumentException("지원되지 않는 라우팅 키: " + routingKey);
	}

}
