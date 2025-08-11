package com.example.surveyapi.domain.survey.application;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyRepository surveyRepository;
	private final ProjectPort projectPort;

	@Transactional
	public Long create(
		String authHeader,
		Long projectId,
		Long creatorId,
		CreateSurveyRequest request
	) {
		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, projectId, creatorId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}

		ProjectStateDto projectState = projectPort.getProjectState(authHeader, projectId);
		if (projectState.isClosed()) {
			throw new CustomException(CustomErrorCode.INVALID_PROJECT_STATE, "종료된 프로젝트에서는 설문을 생성할 수 없습니다.");
		}

		Survey survey = Survey.create(
			projectId, creatorId,
			request.getTitle(), request.getDescription(), request.getSurveyType(),
			request.getSurveyDuration().toSurveyDuration(), request.getSurveyOption().toSurveyOption(),
			request.getQuestions().stream().map(CreateSurveyRequest.QuestionRequest::toQuestionInfo).toList()
		);
		Survey save = surveyRepository.save(survey);

		return save.getSurveyId();
	}

	@CacheEvict(value = "surveyDetails", key = "#surveyId")
	//TODO 실제 업데이트 적용 컬럼 수 계산하는 쿼리 작성 필요
	@Transactional
	public Long update(String authHeader, Long surveyId, Long userId, UpdateSurveyRequest request) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.CONFLICT, "진행 중인 설문은 수정할 수 없습니다.");
		}

		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, survey.getProjectId(), userId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}

		ProjectStateDto projectState = projectPort.getProjectState(authHeader, survey.getProjectId());
		if (projectState.isClosed()) {
			throw new CustomException(CustomErrorCode.INVALID_PROJECT_STATE, "종료된 프로젝트에서는 설문을 수정할 수 없습니다.");
		}

		Map<String, Object> updateFields = new HashMap<>();

		if (request.getTitle() != null) {
			updateFields.put("title", request.getTitle());
		}
		if (request.getDescription() != null) {
			updateFields.put("description", request.getDescription());
		}
		if (request.getSurveyType() != null) {
			updateFields.put("type", request.getSurveyType());
		}
		if (request.getSurveyDuration() != null) {
			updateFields.put("duration", request.getSurveyDuration().toSurveyDuration());
		}
		if (request.getSurveyOption() != null) {
			updateFields.put("option", request.getSurveyOption().toSurveyOption());
		}
		if (request.getQuestions() != null) {
			updateFields.put("questions",
				request.getQuestions().stream().map(UpdateSurveyRequest.QuestionRequest::toQuestionInfo).toList());
		}

		survey.updateFields(updateFields);
		surveyRepository.update(survey);

		return survey.getSurveyId();
	}

	@Transactional
	public Long delete(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.CONFLICT, "진행 중인 설문은 삭제할 수 없습니다.");
		}

		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, survey.getProjectId(), userId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}

		ProjectStateDto projectState = projectPort.getProjectState(authHeader, survey.getProjectId());
		if (projectState.isClosed()) {
			throw new CustomException(CustomErrorCode.INVALID_PROJECT_STATE, "종료된 프로젝트에서는 설문을 삭제할 수 없습니다.");
		}

		survey.delete();
		surveyRepository.delete(survey);

		return survey.getSurveyId();
	}

	@CacheEvict(value = "surveyDetails", key = "#surveyId")
	@Transactional
	public Long open(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() != SurveyStatus.PREPARING) {
			throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION, "준비 중인 설문만 시작할 수 있습니다.");
		}

		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, survey.getProjectId(), userId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}

		survey.open();
		surveyRepository.stateUpdate(survey);

		return survey.getSurveyId();
	}

	@CacheEvict(value = "surveyDetails", key = "#surveyId")
	@Transactional
	public Long close(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() != SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION, "진행 중인 설문만 종료할 수 있습니다.");
		}

		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, survey.getProjectId(), userId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}

		survey.close();
		surveyRepository.stateUpdate(survey);

		return survey.getSurveyId();
	}
}
