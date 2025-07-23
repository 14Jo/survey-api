package com.example.surveyapi.domain.survey.application;

import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

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
		Survey survey = Survey.create(
			projectId, creatorId,
			request.getTitle(), request.getDescription(), request.getSurveyType(),
			request.getSurveyDuration(), request.getSurveyOption(), request.getQuestions()
		);
		Survey save = surveyRepository.save(survey);

		return save.getSurveyId();
	}

	@Transactional
	public String open(Long surveyId, Long userId) {
		return changeSurveyStatus(surveyId, userId, Survey::open, "설문 시작");
	}

	@Transactional
	public String close(Long surveyId, Long userId) {
		return changeSurveyStatus(surveyId, userId, Survey::close, "설문 종료");
	}

	private String changeSurveyStatus(Long surveyId, Long userId, Consumer<Survey> statusChanger, String message) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY, "사용자가 만든 해당 설문이 없습니다."));
		statusChanger.accept(survey);
		return message;
	}
}
