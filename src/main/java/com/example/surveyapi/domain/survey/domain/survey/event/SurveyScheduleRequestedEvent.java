package com.example.surveyapi.domain.survey.domain.survey.event;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class SurveyScheduleRequestedEvent {

	private final Long surveyId;
	private final Long creatorId;
	private final LocalDateTime startAt;
	private final LocalDateTime endAt;

	public SurveyScheduleRequestedEvent(Long surveyId, Long creatorId, LocalDateTime startAt, LocalDateTime endAt) {
		this.surveyId = surveyId;
		this.creatorId = creatorId;
		this.startAt = startAt;
		this.endAt = endAt;
	}
}