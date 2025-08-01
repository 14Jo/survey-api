package com.example.surveyapi.domain.participation.application.client;

import java.util.List;

public interface SurveyServicePort {
	SurveyDetailDto getSurveyDetail(String authHeader, Long surveyId);

	List<SurveyInfoDto> getSurveyInfoList(String authHeader, List<Long> surveyIds);
}
