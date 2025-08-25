package com.example.surveyapi.survey.domain.query;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyReadRepository {
	List<SurveyReadEntity> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);

	List<SurveyReadEntity> findByProjectIdAndSurveyIdGreaterThanOrderByCreatedAtDesc(
		Long projectId, Long lastSurveyId, Pageable pageable);

	List<SurveyReadEntity> findAll();

	Optional<SurveyReadEntity> findBySurveyId(Long surveyId);

	List<SurveyReadEntity> findBySurveyIdIn(List<Long> surveyIds);

	List<SurveyReadEntity> findByStatus(String status);

	SurveyReadEntity save(SurveyReadEntity surveyRead);

	void saveAll(List<SurveyReadEntity> surveyReads);

	void deleteBySurveyId(Long surveyId);

	void updateStatusBySurveyId(Long surveyId, String status);

	void updateBySurveyId(SurveyReadEntity surveyRead);
}
