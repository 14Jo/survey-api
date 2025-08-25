package com.example.surveyapi.domain.statistic.application.event;

public interface StatisticEventPort {
	void handleParticipationEvent(ParticipationResponses responses);
	void handleSurveyActivateEvent(Long surveyId);
	void handleSurveyDeactivateEvent(Long surveyId);
}
