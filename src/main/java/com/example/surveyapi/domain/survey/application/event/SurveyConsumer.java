package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.command.SurveyService;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.event.SurveyEndDueEvent;
import com.example.surveyapi.global.event.SurveyStartDueEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(
	queues = RabbitConst.QUEUE_NAME_SURVEY
)
public class SurveyConsumer {

	private final SurveyRepository surveyRepository;
	private final SurveyService surveyService;

	//TODO 이벤트 객체 변환 및 기능 구현 필요
	// @RabbitHandler
	// public void handleProjectClosed(Object event) {
	// 	try {
	// 		log.info("이벤트 수신");
	// 		Optional<Survey> surveyOp = surveyRepository.findBySurveyIdAndIsDeletedFalse(event.getSurveyId());
	//
	// 		if (surveyOp.isEmpty())
	// 			return;
	//
	// 		Survey survey = surveyOp.get();
	// 		surveyService.surveyActivator(survey, SurveyStatus.CLOSED.name());
	//
	// 	} catch (Exception e) {
	// 		log.error(e.getMessage(), e);
	// 	}
	// }

	@RabbitHandler
	@Transactional
	public void handleSurveyStart(SurveyStartDueEvent event) {
		log.info("SurveyStartDueEvent 수신: surveyId={}, scheduledAt={}", event.getSurveyId(), event.getScheduledAt());
		Optional<Survey> surveyOp = surveyRepository.findBySurveyIdAndIsDeletedFalse(event.getSurveyId());

		if (surveyOp.isEmpty())
			return;

		Survey survey = surveyOp.get();
		if (survey.getDuration().getStartDate() == null ||
			isDifferentMinute(survey.getDuration().getStartDate(), event.getScheduledAt())) {
			return;
		}

		if (survey.getStatus() == SurveyStatus.PREPARING) {
			survey.open();
			surveyRepository.stateUpdate(survey);
		}
	}

	@RabbitHandler
	@Transactional
	public void handleSurveyEnd(SurveyEndDueEvent event) {
		log.info("SurveyEndDueEvent 수신: surveyId={}, scheduledAt={}", event.getSurveyId(), event.getScheduledAt());
		Optional<Survey> surveyOp = surveyRepository.findBySurveyIdAndIsDeletedFalse(event.getSurveyId());

		if (surveyOp.isEmpty())
			return;

		Survey survey = surveyOp.get();
		if (survey.getDuration().getEndDate() == null ||
			isDifferentMinute(survey.getDuration().getEndDate(), event.getScheduledAt())) {
			return;
		}

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			survey.close();
			surveyRepository.stateUpdate(survey);
		}
	}

	private boolean isDifferentMinute(LocalDateTime activeDate, LocalDateTime scheduledDate) {
		return !activeDate.truncatedTo(ChronoUnit.MINUTES).isEqual(scheduledDate.truncatedTo(ChronoUnit.MINUTES));
	}
}
