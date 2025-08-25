package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.event.outbox.SurveyOutboxEventService;
import com.example.surveyapi.domain.survey.application.qeury.SurveyReadSyncPort;
import com.example.surveyapi.domain.survey.application.qeury.dto.QuestionSyncDto;
import com.example.surveyapi.domain.survey.application.qeury.dto.SurveySyncDto;
import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.CreatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.DeletedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.ScheduleStateChangedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.UpdatedEvent;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.survey.SurveyActivateEvent;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.example.surveyapi.global.event.survey.SurveyEndDueEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyEventListener {

	private final SurveyReadSyncPort surveyReadSync;
	private final SurveyOutboxEventService surveyOutboxEventService;

	@EventListener
	public void handle(ActivateEvent event) {
		log.info("ActivateEvent 수신 - 아웃박스 저장 및 조회 테이블 동기화: surveyId={}, status={}",
			event.getSurveyId(), event.getSurveyStatus());

		SurveyActivateEvent activateEvent = new SurveyActivateEvent(
			event.getSurveyId(),
			event.getCreatorId(),
			event.getSurveyStatus().name(),
			event.getEndTime()
		);

		surveyOutboxEventService.saveActivateEvent(activateEvent);

		surveyReadSync.activateSurveyRead(event.getSurveyId(), event.getSurveyStatus());

		log.info("ActivateEvent 처리 완료: surveyId={}", event.getSurveyId());
	}

	@EventListener
	public void handle(CreatedEvent event) {
		log.info("CreatedEvent 수신 - 지연이벤트 아웃박스 저장 및 읽기 동기화 처리: surveyId={}", event.getSurveyId());

		saveDelayedEvents(event.getSurveyId(), event.getCreatorId(),
			event.getDuration().getStartDate(), event.getDuration().getEndDate());

		List<QuestionSyncDto> questionList = event.getQuestions().stream().map(QuestionSyncDto::from).toList();
		surveyReadSync.surveyReadSync(
			SurveySyncDto.from(
				event.getSurveyId(), event.getProjectId(), event.getTitle(),
				event.getDescription(), event.getStatus(), event.getScheduleState(),
				event.getOption(), event.getDuration()
			),
			questionList);

		log.info("CreatedEvent 처리 완료: surveyId={}", event.getSurveyId());
	}

	@EventListener
	public void handle(UpdatedEvent event) {
		log.info("UpdatedEvent 수신 - 지연이벤트 아웃박스 저장 및 읽기 동기화 처리: surveyId={}", event.getSurveyId());

		saveDelayedEvents(event.getSurveyId(), event.getCreatorId(),
			event.getDuration().getStartDate(), event.getDuration().getEndDate());

		List<QuestionSyncDto> questionList = event.getQuestions().stream().map(QuestionSyncDto::from).toList();
		surveyReadSync.updateSurveyRead(SurveySyncDto.from(
			event.getSurveyId(), event.getProjectId(), event.getTitle(),
			event.getDescription(), event.getStatus(), event.getScheduleState(),
			event.getOption(), event.getDuration()
		));
		surveyReadSync.questionReadSync(event.getSurveyId(), questionList);

		log.info("UpdatedEvent 처리 완료: surveyId={}", event.getSurveyId());
	}

	private void saveDelayedEvents(Long surveyId, Long creatorId, LocalDateTime startDate, LocalDateTime endDate) {
		if (startDate != null) {
			SurveyStartDueEvent startEvent = new SurveyStartDueEvent(surveyId, creatorId, startDate);
			long delayMs = java.time.Duration.between(LocalDateTime.now(), startDate).toMillis();

			surveyOutboxEventService.saveDelayedEvent(
				startEvent,
				RabbitConst.ROUTING_KEY_SURVEY_START_DUE,
				delayMs,
				startDate,
				surveyId
			);
			log.debug("설문 시작 지연 이벤트 아웃박스 저장: surveyId={}, startDate={}", surveyId, startDate);
		}

		if (endDate != null) {
			SurveyEndDueEvent endEvent = new SurveyEndDueEvent(surveyId, creatorId, endDate);
			long delayMs = java.time.Duration.between(LocalDateTime.now(), endDate).toMillis();

			surveyOutboxEventService.saveDelayedEvent(
				endEvent,
				RabbitConst.ROUTING_KEY_SURVEY_END_DUE,
				delayMs,
				endDate,
				surveyId
			);
			log.debug("설문 종료 지연 이벤트 아웃박스 저장: surveyId={}, endDate={}", surveyId, endDate);
		}
	}

	@EventListener
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

	@EventListener
	public void handle(DeletedEvent event) {
		log.info("DeletedEvent 수신 - 조회 테이블에서 설문 삭제 처리: surveyId={}", event.getSurveyId());

		surveyReadSync.deleteSurveyRead(event.getSurveyId());

		log.info("설문 삭제 동기화 완료: surveyId={}", event.getSurveyId());
	}
}


