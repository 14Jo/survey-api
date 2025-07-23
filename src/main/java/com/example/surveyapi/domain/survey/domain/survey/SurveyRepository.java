package com.example.surveyapi.domain.survey.domain.survey;

import java.util.Optional;

public interface SurveyRepository {
	Survey save(Survey survey);

	Optional<Survey> findBySurveyIdAndCreatorId(Long surveyId, Long creatorId);
}
