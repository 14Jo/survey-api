package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.event.enums.OutboxEventStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.domain.survey.domain.dlq.OutboxEvent;
import com.example.surveyapi.global.event.survey.SurveyActivateEvent;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.example.surveyapi.global.event.survey.SurveyEndDueEvent;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyOutboxEventService {

	private final OutboxEventRepository outboxEventRepository;
	private final SurveyEventOrchestrator surveyEventOrchestrator;
	private final ObjectMapper objectMapper;
	private final RabbitTemplate rabbitTemplate;

	@Transactional
	public void saveActivateEvent(SurveyActivateEvent activateEvent) {
		saveEvent(
			"Survey",
			activateEvent.getSurveyId(),
			"SurveyActivated",
			activateEvent,
			RabbitConst.ROUTING_KEY_SURVEY_ACTIVE,
			RabbitConst.EXCHANGE_NAME
		);
	}

	@Transactional
	public void saveDelayedEvent(
		Object event, String routingKey, long delayMs, LocalDateTime scheduledAt, Long surveyId
	) {
		saveDelayedEvent(
			"Survey",
			surveyId,
			"SurveyDelayed",
			event,
			routingKey,
			RabbitConst.DELAYED_EXCHANGE_NAME,
			delayMs,
			scheduledAt
		);
	}

	@Transactional
	public void saveEvent(
		String aggregateType, Long aggregateId, String eventType,
		Object eventData, String routingKey, String exchangeName
	) {
		try {
			String serializedData = objectMapper.writeValueAsString(eventData);
			OutboxEvent outboxEvent = OutboxEvent.create(
				aggregateType, aggregateId, eventType, serializedData, routingKey, exchangeName
			);
			outboxEventRepository.save(outboxEvent);

			log.debug("Survey Outbox 이벤트 저장: aggregateId={}, eventType={}", aggregateId, eventType);
		} catch (JsonProcessingException e) {
			log.error("Survey 이벤트 직렬화 실패: aggregateId={}, eventType={}, error={}",
				aggregateId, eventType, e.getMessage());
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "Survey 이벤트 직렬화 실패 message = " + e.getMessage());
		}
	}

	@Transactional
	public void saveDelayedEvent(
		String aggregateType,
		Long aggregateId,
		String eventType,
		Object eventData,
		String routingKey,
		String exchangeName,
		long delayMs,
		LocalDateTime scheduledAt
	) {
		try {
			String serializedData = objectMapper.writeValueAsString(eventData);
			OutboxEvent outboxEvent = OutboxEvent.createDelayed(
				aggregateType, aggregateId, eventType, serializedData,
				routingKey, exchangeName, delayMs, scheduledAt
			);
			outboxEventRepository.save(outboxEvent);

			log.debug("Survey 지연 Outbox 이벤트 저장: aggregateId={}, eventType={}, scheduledAt={}",
				aggregateId, eventType, scheduledAt);
		} catch (JsonProcessingException e) {
			log.error("Survey 지연 이벤트 직렬화 실패: aggregateId={}, eventType={}, error={}",
				aggregateId, eventType, e.getMessage());
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "Survey 지연 이벤트 직렬화 실패 message = " + e.getMessage());
		}
	}

	@Scheduled(fixedDelay = 5000)
	@Transactional
	public void processSurveyOutboxEvents() {
		try {
			log.info("Survey Outbox 이벤트 처리 시작");

			List<OutboxEvent> pendingEvents = outboxEventRepository.findPendingEvents()
				.stream()
				.filter(event -> "Survey".equals(event.getAggregateType()))
				.toList();

			if (pendingEvents.isEmpty()) {
				log.info("처리할 Survey Outbox 이벤트가 없습니다.");
				return;
			}

			// 5분 이상된 PENDING 이벤트를 FAILED로 변경
			markExpiredEventsAsFailed(pendingEvents);

			// 모든 PENDING 이벤트를 오케스트레이터로 위임
			int delegatedCount = 0;
			for (OutboxEvent event : pendingEvents) {
				if (event.getStatus() == OutboxEventStatus.PENDING) {
					try {
						delegateToOrchestrator(event);
						delegatedCount++;
						log.info("오케스트레이터로 위임 완료: id={}, eventType={}",
							event.getOutboxEventId(), event.getEventType());
					} catch (Exception e) {
						log.error("오케스트레이터 위임 실패: id={}, eventType={}, error={}",
							event.getOutboxEventId(), event.getEventType(), e.getMessage());
					}
				}
			}

			log.info("Survey Outbox 이벤트 처리 완료: 총 이벤트={}, 위임 완료={}",
				pendingEvents.size(), delegatedCount);

		} catch (Exception e) {
			log.error("Survey Outbox 이벤트 처리 중 예외 발생", e);
		}
	}

	/**
	 * 5분 이상된 PENDING 이벤트를 FAILED로 변경
	 */
	private void markExpiredEventsAsFailed(List<OutboxEvent> events) {
		LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);

		for (OutboxEvent event : events) {
			if (event.getStatus() == OutboxEventStatus.PENDING && event.getCreatedAt().isBefore(fiveMinutesAgo)) {

				event.asFailed("5분 시간 초과로 만료됨");
				outboxEventRepository.save(event);

				log.warn("5분 시간 초과로 이벤트 만료: id={}, eventType={}, createdAt={}",
					event.getOutboxEventId(), event.getEventType(), event.getCreatedAt());
			}
		}
	}

	/**
	 * PENDING 이벤트를 오케스트레이터로 위임 (기한 체크 없이)
	 */
	private void delegateToOrchestrator(OutboxEvent event) throws JsonProcessingException {
		if ("SurveyActivated".equals(event.getEventType())) {
			processSurveyActivateEvent(event);
		} else if ("SurveyDelayed".equals(event.getEventType())) {
			processDelayedSurveyEvent(event);
		}
	}

	private void processSurveyActivateEvent(OutboxEvent event) throws JsonProcessingException {
		SurveyActivateEvent surveyEvent = objectMapper.readValue(event.getEventData(), SurveyActivateEvent.class);

		ActivateEvent activateEvent = new ActivateEvent(
			surveyEvent.getSurveyId(),
			surveyEvent.getCreatorId(),
			SurveyStatus.valueOf(surveyEvent.getSurveyStatus()),
			surveyEvent.getEndTime()
		);

		log.debug("오케스트레이터를 통한 설문 활성화 이벤트 처리: surveyId={}", surveyEvent.getSurveyId());

		// 오케스트레이터에서 처리 (성공/실패에 따른 스케줄 상태 변경 포함)
		surveyEventOrchestrator.orchestrateActivateEventWithOutboxCallback(activateEvent, event);
	}

	private void processDelayedSurveyEvent(OutboxEvent event) throws JsonProcessingException {
		if (RabbitConst.ROUTING_KEY_SURVEY_START_DUE.equals(event.getRoutingKey())) {
			SurveyStartDueEvent startEvent = objectMapper.readValue(event.getEventData(), SurveyStartDueEvent.class);
			log.debug("오케스트레이터를 통한 설문 시작 지연 이벤트 처리: surveyId={}", startEvent.getSurveyId());

			// 오케스트레이터에서 처리 (성공/실패에 따른 스케줄 상태 변경 포함)
			surveyEventOrchestrator.orchestrateDelayedEventWithOutboxCallback(
				startEvent.getSurveyId(),
				startEvent.getCreatorId(),
				event.getRoutingKey(),
				event.getScheduledAt(),
				event
			);
		} else if (RabbitConst.ROUTING_KEY_SURVEY_END_DUE.equals(event.getRoutingKey())) {
			SurveyEndDueEvent endEvent = objectMapper.readValue(event.getEventData(), SurveyEndDueEvent.class);
			log.debug("오케스트레이터를 통한 설문 종료 지연 이벤트 처리: surveyId={}", endEvent.getSurveyId());

			// 오케스트레이터에서 처리 (성공/실패에 따른 스케줄 상태 변경 포함)
			surveyEventOrchestrator.orchestrateDelayedEventWithOutboxCallback(
				endEvent.getSurveyId(),
				endEvent.getCreatorId(),
				event.getRoutingKey(),
				event.getScheduledAt(),
				event
			);
		}
	}

	@Scheduled(cron = "* * 3 * * *")
	@Transactional
	public void cleanupPublishedSurveyEvents() {
		try {
			LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
			List<OutboxEvent> oldEvents = outboxEventRepository.findPublishedEventsOlderThan(cutoffDate)
				.stream()
				.filter(event -> "Survey".equals(event.getAggregateType()))
				.toList();

			if (!oldEvents.isEmpty()) {
				outboxEventRepository.deleteAll(oldEvents);
				log.info("오래된 Survey Outbox 이벤트 정리 완료: 삭제된 이벤트 수={}", oldEvents.size());
			}
		} catch (Exception e) {
			log.error("Survey Outbox 이벤트 정리 중 오류 발생", e);
		}
	}

	private Object deserializeToActualEventType(String eventData, String eventType) throws JsonProcessingException {
		return switch (eventType) {
			case "SurveyActivated" -> objectMapper.readValue(eventData, SurveyActivateEvent.class);
			case "SurveyDelayed" -> {
				if (eventData.contains("startDate")) {
					yield objectMapper.readValue(eventData, SurveyStartDueEvent.class);
				} else {
					yield objectMapper.readValue(eventData, SurveyEndDueEvent.class);
				}
			}
			default -> throw new CustomException(CustomErrorCode.SERVER_ERROR, "지원하지 않는 이벤트 타입: " + eventType);
		};
	}
}