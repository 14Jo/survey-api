package com.example.surveyapi.domain.survey.domain.query.dto;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyTitle {
	private Long surveyId;
	private String title;
	private SurveyStatus status;
	private SurveyDuration duration;
}
