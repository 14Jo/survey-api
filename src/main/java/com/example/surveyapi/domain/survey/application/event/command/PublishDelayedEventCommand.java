package com.example.surveyapi.domain.survey.application.event.command;

import com.example.surveyapi.domain.survey.application.event.SurveyEventPublisherPort;
import com.example.surveyapi.domain.survey.application.event.SurveyFallbackService;
import com.example.surveyapi.global.event.survey.SurveyEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class PublishDelayedEventCommand implements EventCommand {

	private final SurveyEventPublisherPort publisher;
	private final SurveyEvent event;
	private final String routingKey;
	private final long delayMs;

	@Override
	public void execute() throws Exception {
		try {
			log.info("지연 이벤트 발행 시작: routingKey={}, delayMs={}", routingKey, delayMs);

			publisher.publishDelayed(event, routingKey, delayMs);

			log.info("지연 이벤트 발행 완료: routingKey={}", routingKey);
		} catch (Exception e) {
			log.error("지연 이벤트 발행 실패: routingKey={}, error={}", routingKey, e.getMessage());
			throw e;
		}
	}

	@Override
	public void compensate(Exception ex) {
		log.warn("지연 이벤트 발행 실패 - 보상 작업 실행: routingKey={}, cause={}",
			routingKey, ex.getMessage());
	}

	@Override
	public String getCommandId() {
		return "PUBLISH_DELAYED_" + routingKey + "_" + System.currentTimeMillis();
	}


}
