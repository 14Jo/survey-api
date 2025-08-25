package com.example.surveyapi.survey.domain.survey.event;

import java.time.LocalDateTime;

import com.example.surveyapi.survey.domain.survey.enums.SurveyStatus;

import lombok.Getter;

@Getter
public class ActivateEvent {
	private Long surveyId;
	private Long creatorId;
	private SurveyStatus surveyStatus;
	private LocalDateTime endTime;

	public ActivateEvent(Long surveyId, Long creatorId, SurveyStatus surveyStatus, LocalDateTime endTime) {
		this.surveyId = surveyId;
		this.creatorId = creatorId;
		this.surveyStatus = surveyStatus;
		this.endTime = endTime;
	}
}
