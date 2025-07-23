package com.example.surveyapi.domain.survey.application.response;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
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

	public static SearchSurveyDtailResponse from(SurveyDetail surveyDetail) {
		return new SearchSurveyDtailResponse(
			surveyDetail.getTitle(),
			surveyDetail.getDescription(),
			surveyDetail.getDuration(),
			surveyDetail.getOption(),
			surveyDetail.getQuestions()
		);
	}
}