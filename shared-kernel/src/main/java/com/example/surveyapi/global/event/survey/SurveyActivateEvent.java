package com.example.surveyapi.global.event.survey;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class SurveyActivateEvent implements SurveyEvent {

	private Long surveyId;
	private Long creatorId;
	private String surveyStatus;
	private LocalDateTime endTime;

	public SurveyActivateEvent(Long surveyId, Long creatorId, String surveyStatus, LocalDateTime endTime) {
		this.surveyId = surveyId;
		this.creatorId = creatorId;
		this.surveyStatus = surveyStatus;
		this.endTime = endTime;
	}
}
