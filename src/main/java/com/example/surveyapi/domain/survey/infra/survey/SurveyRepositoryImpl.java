package com.example.surveyapi.domain.survey.infra.survey;

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
}


