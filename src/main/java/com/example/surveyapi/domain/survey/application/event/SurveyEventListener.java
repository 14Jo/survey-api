package com.example.surveyapi.domain.survey.application.event;

import org.springframework.data.domain.AbstractAggregateRoot;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.event.SurveyActivateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SurveyEventListener extends AbstractAggregateRoot {

	private final RabbitPublisherPort rabbitPublisher;
	private final ObjectMapper objectMapper;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(SurveyActivateEvent event) {
		rabbitPublisher.publish(event, EventCode.SURVEY_ACTIVATED);
	}


}
