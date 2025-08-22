package com.example.surveyapi.domain.participation.application.event;

import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.participation.ParticipationGlobalEvent;

public interface ParticipationEventPublisherPort {

	void publish(ParticipationGlobalEvent event, EventCode key);
}
