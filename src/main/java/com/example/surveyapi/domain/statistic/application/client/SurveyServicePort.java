package com.example.surveyapi.domain.statistic.application.client;

import com.example.surveyapi.domain.statistic.application.client.dto.SurveyDetailDto;

public interface SurveyServicePort {

	SurveyDetailDto getSurveyDetail(String authHeader, Long surveyId);
}