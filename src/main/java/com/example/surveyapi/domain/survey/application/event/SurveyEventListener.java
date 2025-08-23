package com.example.surveyapi.domain.survey.application.event;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
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
import com.example.surveyapi.global.event.survey.SurveyEndDueEvent;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.example.surveyapi.global.event.survey.SurveyEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyEventListener {

	private final RabbitTemplate rabbitTemplate;
	private final SurveyEventPublisherPort rabbitPublisher;
	private final ObjectMapper objectMapper;
	private final SurveyFallbackService fallbackService;

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
		log.info("=== 스케줄 변경 수신 ===");
		filterDelay(event.getSurveyId(), event.getCreatorId(), event.getStartAt(), event.getEndAt());
	}

	private void filterDelay(Long surveyId, Long creatorId, LocalDateTime startAt, LocalDateTime endAt) {
		LocalDateTime now = LocalDateTime.now();

		if (startAt != null && startAt.isAfter(now)) {
			long delayMs = Duration.between(now, startAt).toMillis();
			publishDelayed(new SurveyStartDueEvent(surveyId, creatorId, startAt),
				RabbitConst.ROUTING_KEY_SURVEY_START_DUE, delayMs);
		}

		if (endAt != null && endAt.isAfter(now)) {
			long delayMs = Duration.between(now, endAt).toMillis();
			publishDelayed(new SurveyStartDueEvent(surveyId, creatorId, endAt),
				RabbitConst.ROUTING_KEY_SURVEY_END_DUE, delayMs);
		}
	}

	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2.0)
	)
	public void publishDelayed(SurveyEvent event, String routingKey, long delayMs) {
		try {
			log.info("지연 이벤트 발행: routingKey={}, delayMs={}", routingKey, delayMs);
			Map<String, Object> headers = new HashMap<>();
			headers.put("x-delay", delayMs);
			rabbitTemplate.convertAndSend(RabbitConst.DELAYED_EXCHANGE_NAME, routingKey, event, message -> {
				message.getMessageProperties().getHeaders().putAll(headers);
				return message;
			});
			log.info("지연 이벤트 발행 성공: routingKey={}", routingKey);
		} catch (Exception e) {
			log.error("지연 이벤트 발행 실패: routingKey={}, error={}", routingKey, e.getMessage());
			throw e;
		}
	}
	
	@Recover
	public void recoverPublishDelayed(Exception ex, SurveyEvent event, String routingKey, long delayMs) {
		log.error("지연 이벤트 발행 최종 실패 - 풀백 실행: routingKey={}, error={}", routingKey, ex.getMessage());
		fallbackService.handleFailedEvent(event, routingKey, ex.getMessage());
	}
}