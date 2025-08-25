package com.example.surveyapi.domain.statistic.application.client;

public interface SurveyServicePort {

	SurveyDetailDto getSurveyDetail(String authHeader, Long surveyId);
}