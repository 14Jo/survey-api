package com.example.surveyapi.domain.survey.domain.question;

import java.util.List;

public interface QuestionRepository {
	Question save(Question question);

	void saveAll(List<Question> questions);

	List<Question> findAllBySurveyId(Long surveyId);
}
