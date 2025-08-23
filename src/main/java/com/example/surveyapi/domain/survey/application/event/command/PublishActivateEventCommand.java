package com.example.surveyapi.domain.survey.application.event.command;

import com.example.surveyapi.domain.survey.application.event.SurveyEventPublisherPort;
import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.survey.SurveyActivateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 설문 활성화 이벤트 발행 명령
 */
@Slf4j
@RequiredArgsConstructor
public class PublishActivateEventCommand implements EventCommand {

	private final SurveyEventPublisherPort publisher;
	private final ObjectMapper objectMapper;
	private final ActivateEvent activateEvent;

	@Override
	public void execute() throws Exception {
		try {
			log.info("설문 활성화 이벤트 발행 시작: surveyId={}", activateEvent.getSurveyId());

			SurveyActivateEvent surveyActivateEvent = objectMapper.convertValue(activateEvent,
				SurveyActivateEvent.class);
			publisher.publish(surveyActivateEvent, EventCode.SURVEY_ACTIVATED);

			log.info("설문 활성화 이벤트 발행 완료: surveyId={}", activateEvent.getSurveyId());
		} catch (Exception e) {
			log.error("설문 활성화 이벤트 발행 실패: surveyId={}, error={}",
				activateEvent.getSurveyId(), e.getMessage());
			throw e;
		}
	}

	@Override
	public void compensate(Exception cause) {
		log.warn("설문 활성화 이벤트 발행 실패 - 보상 작업 실행: surveyId={}, cause={}",
			activateEvent.getSurveyId(), cause.getMessage());

		// TODO: 필요시 보상 로직 구현 (예: 상태 롤백, 알림 등)
		// 현재는 로깅만 수행
	}

	@Override
	public String getCommandId() {
		return "PUBLISH_ACTIVATE_" + activateEvent.getSurveyId();
	}

	
}
