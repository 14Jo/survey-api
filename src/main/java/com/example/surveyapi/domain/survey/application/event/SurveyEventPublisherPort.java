package com.example.surveyapi.domain.survey.application.event;

import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.survey.SurveyEvent;

public interface SurveyEventPublisherPort {

	void publish(SurveyEvent event, EventCode key);

	void publishDelayed(SurveyEvent event, String routingKey, long delayMs);
}
