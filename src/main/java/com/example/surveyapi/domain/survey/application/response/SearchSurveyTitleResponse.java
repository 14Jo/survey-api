package com.example.surveyapi.domain.survey.application.response;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchSurveyTitleResponse {
	private Long surveyId;
	private String title;
	private SurveyStatus status;
	private SurveyDuration duration;
}
