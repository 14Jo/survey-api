package com.example.surveyapi.domain.survey.infra.survey;

import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.infra.survey.jpa.JpaSurveyRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SurveyRepositoryImpl implements SurveyRepository {

	private final JpaSurveyRepository jpaRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public Survey save(Survey survey) {
		Survey save = jpaRepository.save(survey);
		saveEventPublish(survey);
		return save;
	}

	@Override
	public Optional<Survey> findBySurveyIdAndCreatorId(Long surveyId, Long creatorId) {
		return jpaRepository.findBySurveyIdAndCreatorId(surveyId, creatorId);
	}

	private void saveEventPublish(Survey survey) {
		survey.saved();
		eventPublisher.publishEvent(survey.getCreatedEvent());
		survey.published();
	}
}


