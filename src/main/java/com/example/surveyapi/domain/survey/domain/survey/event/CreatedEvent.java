package com.example.surveyapi.domain.survey.domain.survey.event;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

public class CreatedEvent {
	Survey survey;

	public CreatedEvent(Survey survey) {
		this.survey = survey;
	}

	public Long getSurveyId() {
		return survey.getSurveyId();
	}

	public Long getCreatorId() {
		return survey.getCreatorId();
	}

	public LocalDateTime getStartAt() {
		return survey.getDuration().getStartDate();
	}

	public LocalDateTime getEndAt() {
		return survey.getDuration().getEndDate();
	}
}
