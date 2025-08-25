package com.example.surveyapi.statistic.application.client;

public interface SurveyServicePort {

	SurveyDetailDto getSurveyDetail(String authHeader, Long surveyId);
}