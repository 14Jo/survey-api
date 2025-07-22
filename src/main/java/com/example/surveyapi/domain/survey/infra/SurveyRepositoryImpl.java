package com.example.surveyapi.domain.survey.infra;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.Survey;
import com.example.surveyapi.domain.survey.domain.SurveyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository{

	private final JpaSurveyRepository jpaRepository;

	@Override
	public Survey save(Survey survey) {
		return jpaRepository.save(survey);
	}
}


