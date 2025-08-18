package com.example.surveyapi.domain.survey.application.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.event.SurveyEndDueEvent;
import com.example.surveyapi.global.event.SurveyStartDueEvent;
import com.example.surveyapi.global.exception.CustomException;

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

	//TODO 이벤트 객체 변환 및 기능 구현 필요
	@RabbitHandler
	public void handleProjectClosed(Object event) {
		try {
			log.info("이벤트 수신");

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@RabbitHandler
	@Transactional
	public void handleSurveyStart(SurveyStartDueEvent event) {
		log.info("SurveyStartDueEvent 수신: surveyId={}, scheduledAt={}", event.getSurveyId(), event.getScheduledAt());
		Survey survey = surveyRepository.findBySurveyIdAndIsDeletedFalse(event.getSurveyId())
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getDuration().getStartDate() == null ||
			!survey.getDuration().getStartDate().isEqual(event.getScheduledAt())) {
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
		Survey survey = surveyRepository.findBySurveyIdAndIsDeletedFalse(event.getSurveyId())
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getDuration().getEndDate() == null ||
			!survey.getDuration().getEndDate().isEqual(event.getScheduledAt())) {
			return;
		}

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			survey.close();
			surveyRepository.stateUpdate(survey);
		}
	}
}
