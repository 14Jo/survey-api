package com.example.surveyapi.domain.participation.application.event;

import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.ParticipationGlobalEvent;

public interface ParticipationEventPublisherPort {

	void publish(ParticipationGlobalEvent event, EventCode key);
}
