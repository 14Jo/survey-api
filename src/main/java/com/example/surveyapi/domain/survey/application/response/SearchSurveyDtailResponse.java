package com.example.surveyapi.domain.survey.application.response;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SearchSurveyDtailResponse {
	private String title;
	private String description;
	private SurveyDuration duration;
	private SurveyOption option;
	private List<QuestionInfo> questions;
}