package com.example.surveyapi.survey.application.dto.response;

import java.util.List;

import com.example.surveyapi.survey.domain.query.dto.SurveyStatusList;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchSurveyStatusResponse {
	private List<Long> surveyIds;

	public static SearchSurveyStatusResponse from(SurveyStatusList surveyStatusList) {
		SearchSurveyStatusResponse searchSurveyStatusResponse = new SearchSurveyStatusResponse();
		searchSurveyStatusResponse.surveyIds = surveyStatusList.getSurveyIds();
		return searchSurveyStatusResponse;
	}

	public static SearchSurveyStatusResponse from(List<Long> surveyIds) {
		SearchSurveyStatusResponse searchSurveyStatusResponse = new SearchSurveyStatusResponse();
		searchSurveyStatusResponse.surveyIds = surveyIds;
		return searchSurveyStatusResponse;
	}
}
