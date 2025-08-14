package com.example.surveyapi.domain.survey.application.event;

import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.SurveyEvent;

public interface SurveyEventPublisherPort {

	void publish(SurveyEvent event, EventCode key);
}
