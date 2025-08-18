package com.example.surveyapi.domain.survey.application.event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyScheduleRequestedEvent;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.event.SurveyActivateEvent;
import com.example.surveyapi.global.event.SurveyEndDueEvent;
import com.example.surveyapi.global.event.SurveyStartDueEvent;
import com.example.surveyapi.global.model.SurveyEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SurveyEventListener {

	private final RabbitTemplate rabbitTemplate;
	private final SurveyEventPublisherPort rabbitPublisher;
	private final ObjectMapper objectMapper;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ActivateEvent event) {
		SurveyActivateEvent surveyActivateEvent = objectMapper.convertValue(event, SurveyActivateEvent.class);
		rabbitPublisher.publish(surveyActivateEvent, EventCode.SURVEY_ACTIVATED);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(SurveyScheduleRequestedEvent event) {
		LocalDateTime now = LocalDateTime.now();
		if (event.getStartAt() != null && event.getStartAt().isAfter(now)) {
			long delayMs = Duration.between(now, event.getStartAt()).toMillis();
			publishDelayed(new SurveyStartDueEvent(event.getSurveyId(), event.getCreatorId(), event.getStartAt()),
				RabbitConst.ROUTING_KEY_SURVEY_START_DUE, delayMs);
		}

		if (event.getEndAt() != null && event.getEndAt().isAfter(now)) {
			long delayMs = Duration.between(now, event.getEndAt()).toMillis();
			publishDelayed(new SurveyEndDueEvent(event.getSurveyId(), event.getCreatorId(), event.getEndAt()),
				RabbitConst.ROUTING_KEY_SURVEY_END_DUE, delayMs);
		}
	}

	private void publishDelayed(SurveyEvent event, String routingKey, long delayMs) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("x-delay", delayMs);
		rabbitTemplate.convertAndSend(RabbitConst.DELAYED_EXCHANGE_NAME, routingKey, event, message -> {
			message.getMessageProperties().getHeaders().putAll(headers);
			return message;
		});
	}

}
