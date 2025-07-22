package com.example.surveyapi.domain.survey.application;

import java.time.LocalDateTime;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyCreatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public Long create(
		Long projectId,
		Long creatorId,
		CreateSurveyRequest request
	) {
		SurveyStatus status = decideStatus(request.getStartDate());
		SurveyOption option = new SurveyOption(request.isAnonymous(), request.isAllowMultiple(), request.isAllowResponseUpdate());
		SurveyDuration duration = new SurveyDuration(request.getStartDate(), request.getEndDate());

		Survey survey = Survey.create(
			projectId, creatorId,
			request.getTitle(), request.getDescription(), request.getSurveyType(),
			status, option, duration
		);
		Survey save = surveyRepository.save(survey);

		eventPublisher.publishEvent(new SurveyCreatedEvent(save.getSurveyId(), request.getQuestions()));

		return save.getSurveyId();
	}

	private SurveyStatus decideStatus(LocalDateTime startDate) {
		LocalDateTime now = LocalDateTime.now();
		if (startDate.isAfter(now)) {
			return SurveyStatus.PREPARING;
		} else {
			return SurveyStatus.IN_PROGRESS;
		}
	}
}
