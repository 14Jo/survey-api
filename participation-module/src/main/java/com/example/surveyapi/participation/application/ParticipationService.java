package com.example.surveyapi.participation.application;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.participation.application.client.SurveyDetailDto;
import com.example.surveyapi.participation.application.client.SurveyServicePort;
import com.example.surveyapi.participation.application.client.UserServicePort;
import com.example.surveyapi.participation.application.client.UserSnapshotDto;
import com.example.surveyapi.participation.application.client.enums.SurveyApiQuestionType;
import com.example.surveyapi.participation.application.client.enums.SurveyApiStatus;
import com.example.surveyapi.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.participation.domain.command.ResponseData;
import com.example.surveyapi.participation.domain.participation.Participation;
import com.example.surveyapi.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ParticipationService {

	private final ParticipationRepository participationRepository;
	private final SurveyServicePort surveyPort;
	private final UserServicePort userPort;
	private final TaskExecutor taskExecutor;

	public ParticipationService(ParticipationRepository participationRepository,
		SurveyServicePort surveyPort,
		UserServicePort userPort,
		@Qualifier("externalAPI") TaskExecutor taskExecutor
	) {
		this.participationRepository = participationRepository;
		this.surveyPort = surveyPort;
		this.userPort = userPort;
		this.taskExecutor = taskExecutor;
	}

	@Transactional
	public Long create(String authHeader, Long surveyId, Long userId, CreateParticipationRequest request) {
		log.debug("설문 참여 생성 시작. surveyId: {}, userId: {}", surveyId, userId);
		long totalStartTime = System.currentTimeMillis();

		validateParticipationDuplicated(surveyId, userId);

		List<ResponseData> responseDataList = request.getResponseDataList();

		CompletableFuture<SurveyDetailDto> futureSurveyDetail = CompletableFuture.supplyAsync(
			() -> surveyPort.getSurveyDetail(authHeader, surveyId), taskExecutor).orTimeout(3, TimeUnit.SECONDS);

		CompletableFuture<UserSnapshotDto> futureUserSnapshot = CompletableFuture.supplyAsync(
			() -> userPort.getParticipantInfo(authHeader, userId), taskExecutor).orTimeout(3, TimeUnit.SECONDS);

		final SurveyDetailDto surveyDetail;
		final UserSnapshotDto userSnapshot;

		try {
			surveyDetail = futureSurveyDetail.get();
			userSnapshot = futureUserSnapshot.get();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("비동기 호출 중 인터럽트 발생", e);
			futureSurveyDetail.cancel(true);
			futureUserSnapshot.cancel(true);
			throw new CustomException(CustomErrorCode.EXTERNAL_API_ERROR);
		} catch (ExecutionException e) {
			log.error("비동기 호출 실패", e);
			futureSurveyDetail.cancel(true);
			futureUserSnapshot.cancel(true);
			throw new CustomException(CustomErrorCode.EXTERNAL_API_ERROR);
		}

		validateSurveyActive(surveyDetail);
		validateResponses(responseDataList, surveyDetail.getQuestions());

		ParticipantInfo participantInfo = ParticipantInfo.of(userSnapshot.getBirth(), userSnapshot.getGender(),
			userSnapshot.getRegion());

		Participation participation = Participation.create(userId, surveyId, participantInfo, responseDataList);
		Participation savedParticipation = participationRepository.save(participation);

		savedParticipation.registerCreatedEvent();

		participationRepository.save(savedParticipation);

		long totalEndTime = System.currentTimeMillis();
		log.debug("설문 참여 생성 완료. 총 처리 시간: {}ms", (totalEndTime - totalStartTime));

		return savedParticipation.getId();
	}

	@Transactional
	public void update(String authHeader, Long userId, Long participationId,
		CreateParticipationRequest request) {
		log.debug("설문 참여 수정 시작. participationId: {}, userId: {}", participationId, userId);
		long totalStartTime = System.currentTimeMillis();

		List<ResponseData> responseDataList = request.getResponseDataList();
		Participation participation = getParticipationOrThrow(participationId);

		participation.validateOwner(userId);

		SurveyDetailDto surveyDetail = surveyPort.getSurveyDetail(authHeader, participation.getSurveyId());

		validateSurveyActive(surveyDetail);
		validateAllowUpdate(surveyDetail);
		validateResponses(responseDataList, surveyDetail.getQuestions());
		participation.update(responseDataList);

		long totalEndTime = System.currentTimeMillis();
		log.debug("설문 참여 수정 완료. 총 처리 시간: {}ms", (totalEndTime - totalStartTime));
	}

	/*
	private 메소드 정의
	 */
	private void validateParticipationDuplicated(Long surveyId, Long userId) {
		if (participationRepository.exists(surveyId, userId)) {
			throw new CustomException(CustomErrorCode.SURVEY_ALREADY_PARTICIPATED);
		}
	}

	private void validateSurveyActive(SurveyDetailDto surveyDetail) {
		if (!(surveyDetail.getStatus().equals(SurveyApiStatus.IN_PROGRESS)
			&& surveyDetail.getDuration().getEndDate().isAfter(LocalDateTime.now()))) {

			throw new CustomException(CustomErrorCode.SURVEY_NOT_ACTIVE);
		}
	}

	private Participation getParticipationOrThrow(Long participationId) {
		return participationRepository.findById(participationId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION));
	}

	private void validateAllowUpdate(SurveyDetailDto surveyDetail) {
		if (!surveyDetail.getOption().isAllowResponseUpdate()) {
			throw new CustomException(CustomErrorCode.CANNOT_UPDATE_RESPONSE);
		}
	}

	private void validateResponses(
		List<ResponseData> responses,
		List<SurveyDetailDto.QuestionValidationInfo> questions
	) {
		Map<Long, ResponseData> responseMap = responses.stream()
			.collect(Collectors.toMap(ResponseData::getQuestionId, r -> r));

		// 응답한 questionIds와 설문의 questionIds가 일치하는지 검증, answer = null 이여도 questionId는 존재해야 한다.
		if (responseMap.size() != questions.size() || !responseMap.keySet().equals(
			questions.stream()
				.map(SurveyDetailDto.QuestionValidationInfo::getQuestionId)
				.collect(Collectors.toSet())
		)) {
			throw new CustomException(CustomErrorCode.INVALID_SURVEY_QUESTION);
		}

		for (SurveyDetailDto.QuestionValidationInfo question : questions) {
			ResponseData response = responseMap.get(question.getQuestionId());
			Map<String, Object> answer = response.getAnswer();
			SurveyApiQuestionType questionType = question.getQuestionType();

			switch (questionType) {
				case SINGLE_CHOICE: {
					if (!answer.containsKey("choice") || !(answer.get("choice") instanceof List<?> choiceList)
						|| choiceList.size() > 1) {
						log.error("INVALID_ANSWER_TYPE ERROR: not choice, questionId = {}", question.getQuestionId());
						throw new CustomException(CustomErrorCode.INVALID_ANSWER_TYPE);
					}

					if (choiceList.isEmpty() && question.getIsRequired()) {
						log.error("REQUIRED_QUESTION_NOT_ANSWERED ERROR: questionId = {}",
							question.getQuestionId());
						throw new CustomException(CustomErrorCode.REQUIRED_QUESTION_NOT_ANSWERED);
					}
					Set<Integer> validateChoiceIds = question.getChoices().stream()
						.map(SurveyDetailDto.ChoiceNumber::getChoiceId).collect(Collectors.toSet());

					for (Object choice : choiceList) {
						if (!(choice instanceof Integer choiceId) || !validateChoiceIds.contains(choiceId)) {
							log.error("INVALID_CHOICE_ID ERROR: questionId = {}, choiceId = {}",
								question.getQuestionId(), choice instanceof Integer choiceId);
							throw new CustomException(CustomErrorCode.INVALID_CHOICE_ID);
						}
					}
					break;
				}
				case MULTIPLE_CHOICE: {
					if (!answer.containsKey("choices") ||
						!(answer.get("choices") instanceof List<?> choiceList)) {
						log.error("INVALID_ANSWER_TYPE ERROR: not choices, questionId = {}", question.getQuestionId());
						throw new CustomException(CustomErrorCode.INVALID_ANSWER_TYPE);
					}

					if (choiceList.isEmpty() && question.getIsRequired()) {
						log.error("REQUIRED_QUESTION_NOT_ANSWERED ERROR: questionId = {}",
							question.getQuestionId());
						throw new CustomException(CustomErrorCode.REQUIRED_QUESTION_NOT_ANSWERED);
					}
					Set<Integer> validateChoiceIds = question.getChoices().stream()
						.map(SurveyDetailDto.ChoiceNumber::getChoiceId).collect(Collectors.toSet());

					for (Object choice : choiceList) {
						if (!(choice instanceof Integer choiceId) || !validateChoiceIds.contains(choiceId)) {
							log.error("INVALID_CHOICE_ID ERROR: questionId = {}, choiceId = {}",
								question.getQuestionId(), choice instanceof Integer choiceId);
							throw new CustomException(CustomErrorCode.INVALID_CHOICE_ID);
						}
					}
					break;
				}
				case SHORT_ANSWER, LONG_ANSWER: {
					if (!answer.containsKey("textAnswer") || !(answer.get("textAnswer") instanceof String textAnswer)) {
						log.error("INVALID_ANSWER_TYPE ERROR: not textAnswer, questionId = {}",
							question.getQuestionId());
						throw new CustomException(CustomErrorCode.INVALID_ANSWER_TYPE);
					}
					if (textAnswer.isBlank() && question.getIsRequired()) {
						log.error("REQUIRED_QUESTION_NOT_ANSWERED ERROR: questionId = {}",
							question.getQuestionId());
						throw new CustomException(CustomErrorCode.REQUIRED_QUESTION_NOT_ANSWERED);
					}
					break;
				}
			}
		}
	}
}
