package com.example.surveyapi.domain.survey.application.command;

import java.time.LocalDateTime;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.event.outbox.OutboxEventRepository;
import com.example.surveyapi.domain.survey.application.event.SurveyFallbackService;
import com.example.surveyapi.domain.survey.application.event.command.EventCommand;
import com.example.surveyapi.domain.survey.application.event.command.EventCommandFactory;
import com.example.surveyapi.domain.survey.domain.dlq.OutboxEvent;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyEventOrchestrator {

	private final EventCommandFactory commandFactory;
	private final SurveyFallbackService fallbackService;
	private final SurveyRepository surveyRepository;
	private final OutboxEventRepository outboxEventRepository;

	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2.0)
	)
	public void orchestrateActivateEvent(ActivateEvent activateEvent) {
		log.info("설문 활성화 이벤트 오케스트레이션 시작: surveyId={}", activateEvent.getSurveyId());

		EventCommand command = commandFactory.createActivateEventCommand(activateEvent);
		executeCommand(command);

		log.info("설문 활성화 이벤트 오케스트레이션 완료: surveyId={}", activateEvent.getSurveyId());
	}

	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2.0)
	)
	public void orchestrateDelayedEvent(
		Long surveyId,
		Long creatorId,
		String routingKey,
		LocalDateTime scheduledAt
	) {

		log.info("지연 이벤트 오케스트레이션 시작: surveyId={}, routingKey={}", surveyId, routingKey);

		EventCommand command = commandFactory
			.createDelayedEventCommand(surveyId, creatorId, routingKey, scheduledAt);

		executeCommand(command);

		log.info("지연 이벤트 오케스트레이션 완료: surveyId={}, routingKey={}", surveyId, routingKey);
	}

	private void executeCommand(EventCommand command) {
		try {
			log.debug("명령 실행 시작: commandId={}", command.getCommandId());
			command.execute();
			log.debug("명령 실행 완료: commandId={}", command.getCommandId());
		} catch (Exception e) {
			log.error("명령 실행 실패: commandId={}, error={}", command.getCommandId(), e.getMessage());
			command.compensate(e);
			throw new RuntimeException("명령 실행 실패: " + command.getCommandId(), e);
		}
	}

	public void orchestrateActivateEventWithOutboxCallback(ActivateEvent activateEvent, OutboxEvent outboxEvent) {
		try {
			orchestrateActivateEvent(activateEvent);

			markOutboxAsPublishedAndRestoreScheduleIfNeeded(outboxEvent);

		} catch (Exception e) {
			log.error("아웃박스 콜백 활성화 이벤트 실패: surveyId={}, error={}",
				activateEvent.getSurveyId(), e.getMessage());

			fallbackService.handleFinalFailure(activateEvent.getSurveyId(), e.getMessage());
			throw e;
		}
	}

	public void orchestrateDelayedEventWithOutboxCallback(Long surveyId, Long creatorId,
		String routingKey, LocalDateTime scheduledAt, OutboxEvent outboxEvent) {
		try {
			orchestrateDelayedEvent(surveyId, creatorId, routingKey, scheduledAt);

			markOutboxAsPublishedAndRestoreScheduleIfNeeded(outboxEvent);

		} catch (Exception e) {
			log.error("아웃박스 콜백 지연 이벤트 실패: surveyId={}, routingKey={}, error={}",
				surveyId, routingKey, e.getMessage());

			fallbackService.handleFinalFailure(surveyId, e.getMessage());
			throw e;
		}
	}

	private void markOutboxAsPublishedAndRestoreScheduleIfNeeded(OutboxEvent outboxEvent) {
		outboxEvent.asPublish();
		outboxEventRepository.save(outboxEvent);

		LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
		if (outboxEvent.getCreatedAt().isAfter(fiveMinutesAgo)) {
			restoreAutoScheduleMode(outboxEvent.getAggregateId());
		}
	}

	private void restoreAutoScheduleMode(Long surveyId) {
		try {
			Survey survey = surveyRepository.findById(surveyId).orElse(null);
			if (survey != null && survey.getScheduleState() == ScheduleState.MANUAL_CONTROL) {
				survey.restoreAutoScheduleMode("5분 내 이벤트 발행 성공으로 자동 모드 복구");
				surveyRepository.save(survey);

				log.info("스케줄 상태 자동 모드 복구 완료: surveyId={}", surveyId);
			}
		} catch (Exception e) {
			log.error("스케줄 상태 자동 모드 복구 실패: surveyId={}, error={}", surveyId, e.getMessage());
		}
	}

	@Recover
	public void recoverActivateEvent(Exception ex, ActivateEvent activateEvent) {
		log.error("활성화 이벤트 최종 실패: surveyId={}, error={}", activateEvent.getSurveyId(), ex.getMessage());
	}

	@Recover
	public void recoverDelayedEvent(Exception ex, Long surveyId, Long creatorId,
		String routingKey, LocalDateTime scheduledAt) {
		log.error("지연 이벤트 최종 실패 - 스케줄 상태를 수동으로 변경: surveyId={}, routingKey={}, error={}",
			surveyId, routingKey, ex.getMessage());

		fallbackService.handleFinalFailure(surveyId, ex.getMessage());
	}
}
