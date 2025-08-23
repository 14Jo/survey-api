package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;

import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.event.command.EventCommand;
import com.example.surveyapi.domain.survey.application.event.command.EventCommandFactory;
import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.global.event.survey.SurveyEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 설문 이벤트 오케스트레이터
 * 모든 이벤트 발행을 중앙에서 관리하고 조율하는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyEventOrchestrator {

	private final EventCommandFactory commandFactory;
	private final SurveyFallbackService fallbackService;

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
