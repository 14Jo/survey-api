package com.example.surveyapi.survey.infra.survey.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.survey.domain.survey.Survey;

public interface JpaSurveyRepository extends JpaRepository<Survey, Long> {
	Optional<Survey> findBySurveyIdAndCreatorId(Long surveyId, Long creatorId);

	Optional<Survey> findBySurveyIdAndCreatorIdAndIsDeletedFalse(Long surveyId, Long creatorId);

	Optional<Survey> findBySurveyIdAndIsDeletedFalse(Long surveyId);

	List<Survey> findAllByProjectId(Long projectId);
}
