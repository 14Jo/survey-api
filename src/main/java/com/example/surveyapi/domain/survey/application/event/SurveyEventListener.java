package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.survey.application.qeury.SurveyReadSyncPort;
import com.example.surveyapi.domain.survey.application.qeury.dto.QuestionSyncDto;
import com.example.surveyapi.domain.survey.application.qeury.dto.SurveySyncDto;
import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.CreatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.DeletedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.ScheduleStateChangedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.UpdatedEvent;
import com.example.surveyapi.global.event.RabbitConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyEventListener {

	private final SurveyEventOrchestrator orchestrator;
	private final SurveyReadSyncPort surveyReadSync;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ActivateEvent event) {
		log.info("ActivateEvent 수신 - Orchestrator로 위임 및 조회 테이블 동기화: surveyId={}, status={}", 
			event.getSurveyId(), event.getSurveyStatus());

		// 1. 오케스트레이터로 위임 (기존 로직)
		orchestrator.orchestrateActivateEvent(event);

		// 2. 조회 테이블 상태 동기화 (추가 로직)
		surveyReadSync.activateSurveyRead(event.getSurveyId(), event.getSurveyStatus());
		
		log.info("ActivateEvent 처리 완료: surveyId={}", event.getSurveyId());
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(CreatedEvent event) {
		log.info("CreatedEvent 수신 - 지연이벤트 발행 및 읽기 동기화 처리: surveyId={}", event.getSurveyId());
		delayEvent(event.getSurveyId(), event.getCreatorId(), event.getDuration().getStartDate(),
			event.getDuration().getEndDate());

		List<QuestionSyncDto> questionList = event.getQuestions().stream().map(QuestionSyncDto::from).toList();
		surveyReadSync.surveyReadSync(
			SurveySyncDto.from(
				event.getSurveyId(), event.getProjectId(), event.getTitle(),
				event.getDescription(), event.getStatus(), event.getScheduleState(),
				event.getOption(), event.getDuration()
			),
			questionList);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(UpdatedEvent event) {
		log.info("UpdatedEvent 수신 - 지연이벤트 발행 및 읽기 동기화 처리: surveyId={}", event.getSurveyId());
		delayEvent(event.getSurveyId(), event.getCreatorId(), event.getDuration().getStartDate(),
			event.getDuration().getEndDate());

		List<QuestionSyncDto> questionList = event.getQuestions().stream().map(QuestionSyncDto::from).toList();
		surveyReadSync.updateSurveyRead(SurveySyncDto.from(
			event.getSurveyId(), event.getProjectId(), event.getTitle(),
			event.getDescription(), event.getStatus(), event.getScheduleState(),
			event.getOption(), event.getDuration()
		));
		surveyReadSync.questionReadSync(event.getSurveyId(), questionList);
	}

	private void delayEvent(Long surveyId, Long creatorId, LocalDateTime startDate, LocalDateTime endDate) {
		orchestrator.orchestrateDelayedEvent(
			surveyId,
			creatorId,
			RabbitConst.ROUTING_KEY_SURVEY_START_DUE,
			startDate
		);

		orchestrator.orchestrateDelayedEvent(
			surveyId,
			creatorId,
			RabbitConst.ROUTING_KEY_SURVEY_END_DUE,
			endDate
		);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ScheduleStateChangedEvent event) {
		log.info("ScheduleStateChangedEvent 수신 - 스케줄 상태 동기화 처리: surveyId={}, scheduleState={}, reason={}",
			event.getSurveyId(), event.getScheduleState(), event.getChangeReason());

		surveyReadSync.updateScheduleState(
			event.getSurveyId(),
			event.getScheduleState(),
			event.getSurveyStatus()
		);

		log.info("스케줄 상태 동기화 완료: surveyId={}", event.getSurveyId());
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(DeletedEvent event) {
		log.info("DeletedEvent 수신 - 조회 테이블에서 설문 삭제 처리: surveyId={}", event.getSurveyId());

		// 조회 테이블에서 설문 삭제
		surveyReadSync.deleteSurveyRead(event.getSurveyId());

		log.info("설문 삭제 동기화 완료: surveyId={}", event.getSurveyId());
	}
}


