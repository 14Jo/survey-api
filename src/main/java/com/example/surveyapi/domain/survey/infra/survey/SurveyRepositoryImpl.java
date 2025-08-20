package com.example.surveyapi.domain.survey.infra.survey;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.infra.survey.jpa.JpaSurveyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository {

	private final JpaSurveyRepository jpaRepository;

	@Override
	public Survey save(Survey survey) {
		return jpaRepository.save(survey);
	}

	@Override
	public void delete(Survey survey) {
		jpaRepository.save(survey);
	}

	@Override
	public void update(Survey survey) {
		jpaRepository.save(survey);
	}

	@Override
	public void stateUpdate(Survey survey) {
		jpaRepository.save(survey);
	}

	@Override
	public Optional<Survey> findBySurveyIdAndIsDeletedFalse(Long surveyId) {
		return jpaRepository.findBySurveyIdAndIsDeletedFalse(surveyId);
	}

	@Override
	public Optional<Survey> findBySurveyIdAndCreatorIdAndIsDeletedFalse(Long surveyId, Long creatorId) {
		return jpaRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, creatorId);
	}

	@Override
	public Optional<Survey> findById(Long surveyId) {
		return jpaRepository.findById(surveyId);
	}

	@Override
	public List<Survey> findAllByProjectId(Long projectId) {
		return jpaRepository.findAllByProjectId(projectId);
	}
}


