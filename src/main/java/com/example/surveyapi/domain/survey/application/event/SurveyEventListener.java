package com.example.surveyapi.domain.survey.application.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SurveyEventListener {

	private final RabbitPublisherPort rabbitPublisher;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(SurveyEvent event) {
		rabbitPublisher.publish(event, EventCode.SURVEY_ACTIVATED);
	}
}
