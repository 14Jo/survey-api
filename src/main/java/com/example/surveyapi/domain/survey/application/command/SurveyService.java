package com.example.surveyapi.domain.survey.application.command;

import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.qeury.SurveyReadSyncService;
import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.domain.survey.application.qeury.dto.SurveySyncDto;
import com.example.surveyapi.domain.survey.application.command.dto.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.command.dto.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {

	private final SurveyReadSyncService surveyReadSyncService;
	private final SurveyRepository surveyRepository;
	private final ProjectPort projectPort;

	@Transactional
	public Long create(
		String authHeader,
		Long projectId,
		Long creatorId,
		CreateSurveyRequest request
	) {
		validateProjectAccess(authHeader, projectId, creatorId);

		Survey survey = Survey.create(
			projectId, creatorId,
			request.getTitle(), request.getDescription(), request.getSurveyType(),
			request.getSurveyDuration().toSurveyDuration(), request.getSurveyOption().toSurveyOption(),
			request.getQuestions().stream().map(CreateSurveyRequest.QuestionRequest::toQuestionInfo).toList()
		);

		Survey save = surveyRepository.save(survey);
		
		surveyReadSyncService.surveyReadSync(SurveySyncDto.from(survey));

		return save.getSurveyId();
	}

	@Transactional
	public Long update(String authHeader, Long surveyId, Long userId, UpdateSurveyRequest request) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.CONFLICT, "진행 중인 설문은 수정할 수 없습니다.");
		}

		validateProjectAccess(authHeader, survey.getProjectId(), userId);

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
		surveyReadSyncService.updateSurveyRead(SurveySyncDto.from(survey));

		return survey.getSurveyId();
	}

	@Transactional
	public Long delete(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.CONFLICT, "진행 중인 설문은 삭제할 수 없습니다.");
		}

		validateProjectAccess(authHeader, survey.getProjectId(), userId);

		survey.delete();
		surveyRepository.delete(survey);
		surveyReadSyncService.deleteSurveyRead(surveyId);

		return survey.getSurveyId();
	}

	@Transactional
	public void open(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() != SurveyStatus.PREPARING) {
			throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION, "준비 중인 설문만 시작할 수 있습니다.");
		}

		validateProjectMembership(authHeader, survey.getProjectId(), userId);

		survey.open();
		surveyRepository.stateUpdate(survey);
		updateState(surveyId, survey.getStatus());
	}

	@Transactional
	public void close(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() != SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION, "진행 중인 설문만 종료할 수 있습니다.");
		}

		validateProjectMembership(authHeader, survey.getProjectId(), userId);

		survey.close();
		surveyRepository.stateUpdate(survey);
		updateState(surveyId, survey.getStatus());
	}

	private void validateProjectAccess(String authHeader, Long projectId, Long userId) {
		validateProjectState(authHeader, projectId, userId);
		validateProjectMembership(authHeader, projectId, userId);
	}

	private void validateProjectMembership(String authHeader, Long projectId, Long userId) {
		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, projectId, userId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}
	}

	private void validateProjectState(String authHeader, Long projectId, Long userId) {
		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, projectId, userId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}
	}

	private void updateState(Long surveyId, SurveyStatus surveyStatus) {
		surveyReadSyncService.updateSurveyStatus(surveyId, surveyStatus);
	}
}
