package com.example.surveyapi.domain.survey.domain.query;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyReadRepository {
	List<SurveyReadEntity> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);

	@Query("{'projectId': ?0, 'surveyId': {$gt: ?1}}")
	List<SurveyReadEntity> findByProjectIdAndSurveyIdGreaterThanOrderByCreatedAtDesc(
		Long projectId, Long lastSurveyId, Pageable pageable);

	List<SurveyReadEntity> findAll();

	Optional<SurveyReadEntity> findBySurveyId(Long surveyId);

	SurveyReadEntity save(SurveyReadEntity surveyRead);

	void saveAll(List<SurveyReadEntity> surveyReads);
}
