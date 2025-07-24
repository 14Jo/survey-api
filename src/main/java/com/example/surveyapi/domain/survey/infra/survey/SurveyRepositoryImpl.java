package com.example.surveyapi.domain.survey.infra.survey;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.infra.annotation.SurveyCreate;
import com.example.surveyapi.domain.survey.infra.annotation.SurveyDelete;
import com.example.surveyapi.domain.survey.infra.annotation.SurveyUpdate;
import com.example.surveyapi.domain.survey.infra.survey.jpa.JpaSurveyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository {

	private final JpaSurveyRepository jpaRepository;

	@Override
	@SurveyCreate
	public Survey save(Survey survey) {
		return jpaRepository.save(survey);
	}

	@Override
	@SurveyDelete
	public void delete(Survey survey) {
		jpaRepository.save(survey);
	}

	@Override
	@SurveyUpdate
	public void update(Survey survey) {
		jpaRepository.save(survey);
	}

	@Override
	public void stateUpdate(Survey survey) {
		jpaRepository.save(survey);
	}

	@Override
	public Optional<Survey> findBySurveyIdAndCreatorId(Long surveyId, Long creatorId) {
		return jpaRepository.findBySurveyIdAndCreatorId(surveyId, creatorId);
	}
}


