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
import com.example.surveyapi.domain.survey.domain.survey.event.UpdatedEvent;
import com.example.surveyapi.global.event.RabbitConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 설문 도메인 이벤트 리스너
 * 모든 이벤트 처리를 SurveyEventOrchestrator에 위임하여 중앙 집중식 관리
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyEventListener {

	private final SurveyEventOrchestrator orchestrator;
	private final SurveyReadSyncPort surveyReadSync;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ActivateEvent event) {
		log.info("ActivateEvent 수신 - Orchestrator로 위임: surveyId={}", event.getSurveyId());
		orchestrator.orchestrateActivateEvent(event);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(CreatedEvent event) {
		log.info("CreatedEvent 수신 - 지연이벤트 발행 및 읽기 동기화 처리: surveyId={}", event.getSurveyId());
		delayEvent(event.getSurveyId(), event.getCreatorId(), event.getDuration().getStartDate(), event.getDuration().getEndDate());

		// 3. 읽기 동기화
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
		delayEvent(event.getSurveyId(), event.getCreatorId(), event.getDuration().getStartDate(), event.getDuration().getEndDate());

		// 3. 읽기 동기화
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
}