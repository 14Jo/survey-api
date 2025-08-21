package com.example.surveyapi.global.event;

import java.time.LocalDateTime;

import com.example.surveyapi.global.model.SurveyEvent;

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