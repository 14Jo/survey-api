package com.example.surveyapi.domain.survey.domain.query.dto;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.querydsl.core.annotations.QueryProjection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class SurveyTitle {
	private Long surveyId;
	private String title;
	private SurveyOption option;
	private SurveyStatus status;
	private SurveyDuration duration;

	public static SurveyTitle of(Long surveyId, String title, SurveyOption option, SurveyStatus status, SurveyDuration duration) {
		SurveyTitle surveyTitle = new SurveyTitle();
		surveyTitle.surveyId = surveyId;
		surveyTitle.title = title;
		surveyTitle.option = option;
		surveyTitle.status = status;
		surveyTitle.duration = duration;
		return surveyTitle;
	}
}
