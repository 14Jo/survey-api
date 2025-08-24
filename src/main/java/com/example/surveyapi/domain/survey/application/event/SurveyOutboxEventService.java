package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
			log.debug("Survey Outbox 이벤트 처리 시작");

			List<OutboxEvent> pendingEvents = outboxEventRepository.findEventsToProcess(LocalDateTime.now())
				.stream()
				.filter(event -> "Survey".equals(event.getAggregateType()))
				.toList();

			if (pendingEvents.isEmpty()) {
				log.debug("처리할 Survey Outbox 이벤트가 없습니다.");
				return;
			}

			int publishedCount = 0;
			int failedCount = 0;

			for (OutboxEvent event : pendingEvents) {
				try {
					if (event.isReadyForDelivery()) {
						processSurveyEvent(event);
						event.asPublish();
						publishedCount++;
						log.debug("Survey Outbox 이벤트 발행 성공: id={}, eventType={}",
							event.getOutboxEventId(), event.getEventType());
					}
				} catch (Exception e) {
					event.asFailed(e.getMessage());
					failedCount++;
					log.error("Survey Outbox 이벤트 발행 실패: id={}, eventType={}, error={}",
						event.getOutboxEventId(), event.getEventType(), e.getMessage());
				}

				outboxEventRepository.save(event);
			}

			log.info("Survey Outbox 이벤트 처리 완료: 처리대상={}, 성공={}, 실패={}",
				pendingEvents.size(), publishedCount, failedCount);

		} catch (Exception e) {
			log.error("Survey Outbox 이벤트 처리 중 예외 발생", e);
		}
	}

	private void processSurveyEvent(OutboxEvent event) {
		try {
			publishEventToRabbit(event);

			if ("SurveyActivated".equals(event.getEventType())) {
				processSurveyActivateEvent(event);
			} else if ("SurveyDelayed".equals(event.getEventType())) {
				processDelayedSurveyEvent(event);
			}
		} catch (JsonProcessingException e) {
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "Survey 이벤트 역직렬화 실패 message = " + e);
		}
	}

	private void publishEventToRabbit(OutboxEvent event) {
		try {
			Object eventData = objectMapper.readValue(event.getEventData(), Object.class);

			if (event.isDelayedEvent()) {
				publishDelayedEvent(event);
			} else {
				publishImmediateEvent(event);
			}
		} catch (JsonProcessingException e) {
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "Survey 이벤트 역직렬화 실패" + e);
		}
	}

	private void publishImmediateEvent(OutboxEvent event) {
		try {
			Object actualEvent = deserializeToActualEventType(event.getEventData(), event.getEventType());
			rabbitTemplate.convertAndSend(event.getExchangeName(), event.getRoutingKey(), actualEvent);
		} catch (JsonProcessingException e) {
			log.error("이벤트 역직렬화 실패: eventType={}, error={}", event.getEventType(), e.getMessage());
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "이벤트 역직렬화 실패" + e);
		}
	}

	private void publishDelayedEvent(OutboxEvent event) {
		try {
			Object actualEvent = deserializeToActualEventType(event.getEventData(), event.getEventType());
			Map<String, Object> headers = new HashMap<>();
			headers.put("x-delay", event.getDelayMs());

			rabbitTemplate.convertAndSend(event.getExchangeName(), event.getRoutingKey(), actualEvent, message -> {
				message.getMessageProperties().getHeaders().putAll(headers);
				return message;
			});
		} catch (JsonProcessingException e) {
			log.error("지연 이벤트 역직렬화 실패: eventType={}, error={}", event.getEventType(), e.getMessage());
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "지연 이벤트 역직렬화 실패" + e);
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
		surveyEventOrchestrator.orchestrateActivateEvent(activateEvent);
	}

	private void processDelayedSurveyEvent(OutboxEvent event) throws JsonProcessingException {
		if (RabbitConst.ROUTING_KEY_SURVEY_START_DUE.equals(event.getRoutingKey())) {
			SurveyStartDueEvent startEvent = objectMapper.readValue(event.getEventData(), SurveyStartDueEvent.class);
			log.debug("오케스트레이터를 통한 설문 시작 지연 이벤트 처리: surveyId={}", startEvent.getSurveyId());
			surveyEventOrchestrator.orchestrateDelayedEvent(
				startEvent.getSurveyId(),
				startEvent.getCreatorId(),
				event.getRoutingKey(),
				event.getScheduledAt()
			);
		} else if (RabbitConst.ROUTING_KEY_SURVEY_END_DUE.equals(event.getRoutingKey())) {
			SurveyEndDueEvent endEvent = objectMapper.readValue(event.getEventData(), SurveyEndDueEvent.class);
			log.debug("오케스트레이터를 통한 설문 종료 지연 이벤트 처리: surveyId={}", endEvent.getSurveyId());
			surveyEventOrchestrator.orchestrateDelayedEvent(
				endEvent.getSurveyId(),
				endEvent.getCreatorId(),
				event.getRoutingKey(),
				event.getScheduledAt()
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