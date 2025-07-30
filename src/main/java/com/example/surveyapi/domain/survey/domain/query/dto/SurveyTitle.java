package com.example.surveyapi.domain.survey.domain.query.dto;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyTitle {
	private Long surveyId;
	private String title;
	private SurveyStatus status;
	private SurveyDuration duration;

	public static SurveyTitle of(Long surveyId, String title, SurveyStatus status, SurveyDuration duration) {
		SurveyTitle surveyTitle = new SurveyTitle();
		surveyTitle.surveyId = surveyId;
		surveyTitle.title = title;
		surveyTitle.status = status;
		surveyTitle.duration = duration;
		return surveyTitle;
	}
}
