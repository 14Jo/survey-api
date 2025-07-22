package com.example.surveyapi.domain.survey.application;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.domain.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.Survey;
import com.example.surveyapi.domain.survey.domain.SurveyRepository;
import com.example.surveyapi.domain.survey.enums.SurveyStatus;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyRepository surveyRepository;

	@Transactional
	public Long create(
		Long projectId,
		Long creatorId,
		CreateSurveyRequest request
	) {
		SurveyStatus status = decideStatus(request.getStartDate());
		Survey survey = Survey.create(
			projectId, creatorId, request.getTitle(), request.getDescription(), request.getSurveyType(), status,
			request.isAnonymous(), request.isAllowMultiple(), request.isAllowResponseUpdate(), request.getStartDate(),
			request.getEndDate()
		);
		Survey save = surveyRepository.save(survey);
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
