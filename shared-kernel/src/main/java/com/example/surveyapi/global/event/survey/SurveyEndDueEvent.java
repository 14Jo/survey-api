package com.example.surveyapi.global.event.survey;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class SurveyEndDueEvent implements SurveyEvent {
	private final Long surveyId;
	private final Long creatorId;
	private final LocalDateTime scheduledAt;

	public SurveyEndDueEvent(Long surveyId, Long creatorId, LocalDateTime scheduledAt) {
		this.surveyId = surveyId;
		this.creatorId = creatorId;
		this.scheduledAt = scheduledAt;
	}
}