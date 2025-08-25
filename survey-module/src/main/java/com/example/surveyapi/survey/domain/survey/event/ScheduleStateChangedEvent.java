package com.example.surveyapi.survey.domain.survey.event;

import com.example.surveyapi.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.survey.domain.survey.enums.SurveyStatus;

public class ScheduleStateChangedEvent {
	private final Long surveyId;
	private final Long creatorId;
	private final ScheduleState scheduleState;
	private final SurveyStatus surveyStatus;
	private final String changeReason;

	public ScheduleStateChangedEvent(Long surveyId, Long creatorId, ScheduleState scheduleState,
		SurveyStatus surveyStatus, String changeReason
	) {
		this.surveyId = surveyId;
		this.creatorId = creatorId;
		this.scheduleState = scheduleState;
		this.surveyStatus = surveyStatus;
		this.changeReason = changeReason;
	}

	public Long getSurveyId() {
		return surveyId;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public ScheduleState getScheduleState() {
		return scheduleState;
	}

	public SurveyStatus getSurveyStatus() {
		return surveyStatus;
	}

	public String getChangeReason() {
		return changeReason;
	}
}