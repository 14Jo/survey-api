package com.example.surveyapi.domain.survey.application.event;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.CreatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.ScheduleRequestedEvent;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.survey.SurveyActivateEvent;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyEventListener {

	private final SurveyEventPublisherPort rabbitPublisher;
	private final RetryablePublisher retryablePublisher;
	private final ObjectMapper objectMapper;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ActivateEvent event) {
		SurveyActivateEvent surveyActivateEvent = objectMapper.convertValue(event, SurveyActivateEvent.class);
		rabbitPublisher.publish(surveyActivateEvent, EventCode.SURVEY_ACTIVATED);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(CreatedEvent event) {
		filterDelay(event.getSurveyId(), event.getCreatorId(), event.getStartAt(), event.getEndAt());
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ScheduleRequestedEvent event) {
		filterDelay(event.getSurveyId(), event.getCreatorId(), event.getStartAt(), event.getEndAt());
	}

	private void filterDelay(Long surveyId, Long creatorId, LocalDateTime startAt, LocalDateTime endAt) {
		LocalDateTime now = LocalDateTime.now();

		if (startAt != null && startAt.isAfter(now)) {
			long delayMs = Duration.between(now, startAt).toMillis();
			retryablePublisher.publishDelayed(new SurveyStartDueEvent(surveyId, creatorId, startAt),
				RabbitConst.ROUTING_KEY_SURVEY_START_DUE, delayMs);
		}

		if (endAt != null && endAt.isAfter(now)) {
			long delayMs = Duration.between(now, endAt).toMillis();
			retryablePublisher.publishDelayed(new SurveyStartDueEvent(surveyId, creatorId, endAt),
				RabbitConst.ROUTING_KEY_SURVEY_END_DUE, delayMs);
		}
	}
}