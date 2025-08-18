package com.example.surveyapi.domain.survey.infra.survey.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

public interface JpaSurveyRepository extends JpaRepository<Survey, Long> {
	Optional<Survey> findBySurveyIdAndCreatorId(Long surveyId, Long creatorId);

	Optional<Survey> findBySurveyIdAndCreatorIdAndIsDeletedFalse(Long surveyId, Long creatorId);

	Optional<Survey> findBySurveyIdAndIsDeletedFalse(Long surveyId);
}
