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

	public String delete(Long surveyId, Long userId) {
		Survey survey = changeSurveyStatus(surveyId, userId, Survey::delete);
		surveyRepository.delete(survey);

		return "설문 삭제";
	}

	@Transactional
	public String open(Long surveyId, Long userId) {
		Survey survey = changeSurveyStatus(surveyId, userId, Survey::open);
		surveyRepository.stateUpdate(survey);

		return "설문 시작";
	}

	@Transactional
	public String close(Long surveyId, Long userId) {
		Survey survey = changeSurveyStatus(surveyId, userId, Survey::close);
		surveyRepository.stateUpdate(survey);

		return  "설문 종료";
	}

	private Survey changeSurveyStatus(Long surveyId, Long userId, Consumer<Survey> statusChanger) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY, "사용자가 만든 해당 설문이 없습니다."));
		statusChanger.accept(survey);
		return survey;
	}
}
