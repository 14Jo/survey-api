package com.example.surveyapi.domain.survey.application.command;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.domain.survey.application.dto.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.dto.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
		validateProjectAccess(authHeader, projectId, creatorId);

		Survey survey = Survey.create(
			projectId, creatorId,
			request.getTitle(), request.getDescription(), request.getSurveyType(),
			request.getSurveyDuration().toSurveyDuration(), request.getSurveyOption().toSurveyOption(),
			request.getQuestions().stream().map(CreateSurveyRequest.QuestionRequest::toQuestionInfo).toList()
		);

		Survey save = surveyRepository.save(survey);

		return save.getSurveyId();
	}

	@CacheEvict(value = {"surveyDetails", "surveyInfo"}, key = "#surveyId")
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
		survey.applyDurationChange(survey.getDuration(), LocalDateTime.now());
		surveyRepository.update(survey);

		return survey.getSurveyId();
	}

	@CacheEvict(value = {"surveyDetails", "surveyInfo"}, key = "#surveyId")
	@Transactional
	public Long delete(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.CONFLICT, "진행 중인 설문은 삭제할 수 없습니다.");
		}

		validateProjectAccess(authHeader, survey.getProjectId(), userId);

		surveyDeleter(survey, surveyId);

		return survey.getSurveyId();
	}

	@CacheEvict(value = {"surveyDetails", "surveyInfo"}, key = "#surveyId")
	@Transactional
	public void open(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() != SurveyStatus.PREPARING) {
			throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION, "준비 중인 설문만 시작할 수 있습니다.");
		}

		validateProjectMembership(authHeader, survey.getProjectId(), userId);

		surveyActivator(survey, SurveyStatus.IN_PROGRESS.name());
	}

	@CacheEvict(value = {"surveyDetails", "surveyInfo"}, key = "#surveyId")
	@Transactional
	public void close(String authHeader, Long surveyId, Long userId) {
		Survey survey = surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(surveyId, userId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		if (survey.getStatus() != SurveyStatus.IN_PROGRESS) {
			throw new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION, "진행 중인 설문만 종료할 수 있습니다.");
		}

		validateProjectMembership(authHeader, survey.getProjectId(), userId);

		surveyActivator(survey, SurveyStatus.CLOSED.name());
	}

	private void validateProjectAccess(String authHeader, Long projectId, Long userId) {
		validateProjectState(authHeader, projectId);
		validateProjectMembership(authHeader, projectId, userId);
	}

	private void validateProjectMembership(String authHeader, Long projectId, Long userId) {
		ProjectValidDto projectValid = projectPort.getProjectMembers(authHeader, projectId, userId);
		if (!projectValid.getValid()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트에 참여하지 않은 사용자입니다.");
		}
	}

	private void validateProjectState(String authHeader, Long projectId) {
		ProjectStateDto projectState = projectPort.getProjectState(authHeader, projectId);
		if (projectState.isClosed()) {
			throw new CustomException(CustomErrorCode.INVALID_PERMISSION, "프로젝트가 종료되었습니다.");
		}
	}

	public void surveyActivator(Survey survey, String activator) {
		if (activator.equals(SurveyStatus.IN_PROGRESS.name())) {
			survey.openAt(LocalDateTime.now());
		}
		if (activator.equals(SurveyStatus.CLOSED.name())) {
			survey.closeAt(LocalDateTime.now());
		}
		surveyRepository.stateUpdate(survey);
		//surveyReadSync.activateSurveyRead(survey.getSurveyId(), survey.getStatus());
	}

	public void surveyDeleter(Survey survey, Long surveyId) {
		survey.delete();
		surveyRepository.delete(survey);
		//surveyReadSync.deleteSurveyRead(surveyId);
	}

	public void surveyDeleteForProject(Long projectId) {
		List<Survey> surveyOp = surveyRepository.findAllByProjectId(projectId);

		surveyOp.forEach(survey -> {
			surveyDeleter(survey, survey.getSurveyId());
		});
	}

	public void processSurveyStart(Long surveyId, LocalDateTime eventScheduledAt) {
		Optional<Survey> surveyOp = surveyRepository.findBySurveyIdAndIsDeletedFalse(surveyId);

		if (surveyOp.isEmpty())
			return;

		Survey survey = surveyOp.get();
		if (isDifferentMinute(survey.getDuration().getStartDate(), eventScheduledAt)) {
			return;
		}

		if (survey.getStatus() == SurveyStatus.PREPARING) {
			survey.openAt(eventScheduledAt);
			surveyRepository.stateUpdate(survey);
		}
	}

	public void processSurveyEnd(Long surveyId, LocalDateTime eventScheduledAt) {
		Optional<Survey> surveyOp = surveyRepository.findBySurveyIdAndIsDeletedFalse(surveyId);

		if (surveyOp.isEmpty())
			return;

		Survey survey = surveyOp.get();
		if (isDifferentMinute(survey.getDuration().getEndDate(), eventScheduledAt)) {
			return;
		}

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			survey.closeAt(eventScheduledAt);
			surveyRepository.stateUpdate(survey);
		}
	}

	private boolean isDifferentMinute(LocalDateTime activeDate, LocalDateTime scheduledDate) {
		return !activeDate.truncatedTo(ChronoUnit.MINUTES).isEqual(scheduledDate.truncatedTo(ChronoUnit.MINUTES));
	}
}
