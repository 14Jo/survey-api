package com.example.surveyapi.domain.participation.application.dto.request;

import java.time.LocalDate;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyInfoOfParticipation {

	private Long surveyId;
	private String surveyTitle;
	private String surveyStatus;
	private LocalDate endDate;
	private boolean allowResponseUpdate;

	public static SurveyInfoOfParticipation of(Long surveyId, String surveyTitle, String surveyStatus,
		LocalDate endDate, boolean allowResponseUpdate) {
		SurveyInfoOfParticipation surveyInfo = new SurveyInfoOfParticipation();
		surveyInfo.surveyId = surveyId;
		surveyInfo.surveyTitle = surveyTitle;
		surveyInfo.surveyStatus = surveyStatus;
		surveyInfo.endDate = endDate;
		surveyInfo.allowResponseUpdate = allowResponseUpdate;

		return surveyInfo;
	}
}
