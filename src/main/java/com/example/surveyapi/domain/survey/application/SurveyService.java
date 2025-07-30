package com.example.surveyapi.domain.survey.application;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.UpdateSurveyRequest;
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
			request.getSurveyDuration().toSurveyDuration(), request.getSurveyOption().toSurveyOption(),
			request.getQuestions().stream().map(CreateSurveyRequest.QuestionRequest::toQuestionInfo).toList()
		);
		Survey save = surveyRepository.save(survey);

		return save.getSurveyId();
	}

	//TODO 실제 업데이트 적용 컬럼 수 계산하는 쿼리 작성 필요
	@Transactional
	public String update(Long surveyId, Long userId, UpdateSurveyRequest request) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		Map<String, Object> updateFields = new HashMap<>();
		int modifiedCount = 0;

		if (request.getTitle() != null) {
			updateFields.put("title", request.getTitle());
			modifiedCount++;
		}
		if (request.getDescription() != null) {
			updateFields.put("description", request.getDescription());
			modifiedCount++;
		}
		if (request.getSurveyType() != null) {
			updateFields.put("type", request.getSurveyType());
			modifiedCount++;
		}
		if (request.getSurveyDuration() != null) {
			updateFields.put("duration", request.getSurveyDuration().toSurveyDuration());
			modifiedCount++;
		}
		if (request.getSurveyOption() != null) {
			updateFields.put("option", request.getSurveyOption().toSurveyOption());
			modifiedCount++;
		}
		if (request.getQuestions() != null) {
			updateFields.put("questions", request.getQuestions().stream().map(UpdateSurveyRequest.QuestionRequest::toQuestionInfo).toList());
		}

		survey.updateFields(updateFields);

		int addedQuestions = (request.getQuestions() != null) ? request.getQuestions().size() : 0;

		surveyRepository.update(survey);

		return String.format("수정: %d개, 질문 추가: %d개", modifiedCount, addedQuestions);
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

		return "설문 종료";
	}

	private Survey changeSurveyStatus(Long surveyId, Long userId, Consumer<Survey> statusChanger) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorId(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY, "사용자가 만든 해당 설문이 없습니다."));
		statusChanger.accept(survey);
		return survey;
	}
}
