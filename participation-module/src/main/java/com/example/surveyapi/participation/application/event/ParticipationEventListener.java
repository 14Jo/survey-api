package com.example.surveyapi.participation.application.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.participation.domain.event.ParticipationCreatedEvent;
import com.example.surveyapi.participation.domain.event.ParticipationEvent;
import com.example.surveyapi.participation.domain.event.ParticipationUpdatedEvent;
import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.participation.ParticipationCreatedGlobalEvent;
import com.example.surveyapi.global.event.participation.ParticipationUpdatedGlobalEvent;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ParticipationEventListener {

	private final ParticipationEventPublisherPort rabbitPublisher;
	private final ObjectMapper objectMapper;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handle(ParticipationEvent event) {
		if (event instanceof ParticipationCreatedEvent) {
			ParticipationCreatedGlobalEvent createdGlobalEvent = objectMapper.convertValue(event,
				new TypeReference<ParticipationCreatedGlobalEvent>() {
				});

			rabbitPublisher.publish(createdGlobalEvent, EventCode.PARTICIPATION_CREATED);
		} else if (event instanceof ParticipationUpdatedEvent) {
			ParticipationUpdatedGlobalEvent updatedGlobalEvent = objectMapper.convertValue(event,
				new TypeReference<ParticipationUpdatedGlobalEvent>() {
				});

			rabbitPublisher.publish(updatedGlobalEvent, EventCode.PARTICIPATION_UPDATED);
		}
	}
}
