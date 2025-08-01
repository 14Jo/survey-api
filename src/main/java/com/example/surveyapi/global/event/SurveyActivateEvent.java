package com.example.surveyapi.global.event;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.Getter;

@Getter
public class SurveyActivateEvent implements SurveyEvent {

	private Long surveyId;
	private SurveyStatus surveyStatus;
	private LocalDateTime endTime;

	public SurveyActivateEvent(Long surveyId, SurveyStatus surveyStatus, LocalDateTime endTime) {
		this.surveyId = surveyId;
		this.surveyStatus = surveyStatus;
		this.endTime = endTime;
	}
}
