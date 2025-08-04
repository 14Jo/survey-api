package com.example.surveyapi.domain.survey.domain.survey;

import java.util.Optional;

public interface SurveyRepository {
	Survey save(Survey survey);

	void delete(Survey survey);

	void update(Survey survey);

	void stateUpdate(Survey survey);

	Optional<Survey> findBySurveyIdAndCreatorId(Long surveyId, Long creatorId);

	Optional<Survey> findBySurveyIdAndCreatorIdAndIsDeletedFalse(Long surveyId, Long creatorId);

	Optional<Survey> findById(Long surveyId);
}
