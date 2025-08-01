package com.example.surveyapi.domain.participation.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.client.SurveyDetailDto;
import com.example.surveyapi.domain.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.domain.participation.application.client.SurveyServicePort;
import com.example.surveyapi.domain.participation.application.client.UserServicePort;
import com.example.surveyapi.domain.participation.application.client.UserSnapshotDto;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiQuestionType;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;
import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.response.AnswerGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.query.QuestionAnswer;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.participation.domain.response.Response;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ParticipationService {

	private final ParticipationRepository participationRepository;
	private final SurveyServicePort surveyPort;
	private final UserServicePort userPort;

	@Transactional
	public Long create(String authHeader, Long surveyId, Long memberId, CreateParticipationRequest request) {
		validateParticipationDuplicated(surveyId, memberId);

		SurveyDetailDto surveyDetail = surveyPort.getSurveyDetail(authHeader, surveyId);

		validateSurveyActive(surveyDetail);

		List<ResponseData> responseDataList = request.getResponseDataList();
		List<SurveyDetailDto.QuestionValidationInfo> questions = surveyDetail.getQuestions();

		// 문항과 답변 유효성 검증
		validateQuestionsAndAnswers(responseDataList, questions);

		UserSnapshotDto userSnapshot = userPort.getParticipantInfo(authHeader, memberId);
		ParticipantInfo participantInfo = ParticipantInfo.of(
			userSnapshot.getBirth(),
			userSnapshot.getGender(),
			userSnapshot.getRegion().getProvince(),
			userSnapshot.getRegion().getDistrict()
		);

		Participation participation = Participation.create(memberId, surveyId, participantInfo, responseDataList);

		Participation savedParticipation = participationRepository.save(participation);

		return savedParticipation.getId();
	}

	@Transactional(readOnly = true)
	public Page<ParticipationInfoResponse> gets(String authHeader, Long memberId, Pageable pageable) {
		Page<ParticipationInfo> participationInfos = participationRepository.findParticipationInfos(memberId,
			pageable);

		List<Long> surveyIds = participationInfos.getContent().stream()
			.map(ParticipationInfo::getSurveyId)
			.toList();

		List<SurveyInfoDto> surveyInfoList = surveyPort.getSurveyInfoList(authHeader, surveyIds);

		List<ParticipationInfoResponse.SurveyInfoOfParticipation> surveyInfoOfParticipations = surveyInfoList.stream()
			.map(ParticipationInfoResponse.SurveyInfoOfParticipation::from)
			.toList();

		Map<Long, ParticipationInfoResponse.SurveyInfoOfParticipation> surveyInfoMap = surveyInfoOfParticipations.stream()
			.collect(Collectors.toMap(
				ParticipationInfoResponse.SurveyInfoOfParticipation::getSurveyId,
				surveyInfo -> surveyInfo
			));

		// TODO: stream 한번만 사용하여서 map 수정
		return participationInfos.map(p -> {
			ParticipationInfoResponse.SurveyInfoOfParticipation surveyInfo = surveyInfoMap.get(p.getSurveyId());

			return ParticipationInfoResponse.of(p, surveyInfo);
		});
	}

	@Transactional(readOnly = true)
	public List<ParticipationGroupResponse> getAllBySurveyIds(List<Long> surveyIds) {
		List<Participation> participationList = participationRepository.findAllBySurveyIdIn(surveyIds);

		// surveyId 기준으로 참여 기록을 Map 으로 그룹핑
		Map<Long, List<Participation>> participationGroupBySurveyId = participationList.stream()
			.collect(Collectors.groupingBy(Participation::getSurveyId));

		List<ParticipationGroupResponse> result = new ArrayList<>();

		for (Long surveyId : surveyIds) {
			List<Participation> participationGroup = participationGroupBySurveyId.getOrDefault(surveyId,
				Collections.emptyList());

			List<ParticipationDetailResponse> participationDtos = new ArrayList<>();

			for (Participation p : participationGroup) {
				List<ParticipationDetailResponse.AnswerDetail> answerDetails = p.getResponses().stream()
					.map(ParticipationDetailResponse.AnswerDetail::from)
					.toList();

				participationDtos.add(ParticipationDetailResponse.from(p));
			}

			result.add(ParticipationGroupResponse.of(surveyId, participationDtos));
		}
		return result;
	}

	@Transactional(readOnly = true)
	public ParticipationDetailResponse get(Long loginMemberId, Long participationId) {
		Participation participation = getParticipationOrThrow(participationId);

		participation.validateOwner(loginMemberId);

		// TODO: 상세 조회에서 수정가능한지 확인하기 위해 Response에 surveyStatus, endDate, allowResponseUpdate을 추가해야하는가 고려

		return ParticipationDetailResponse.from(participation);
	}

	@Transactional
	public void update(String authHeader, Long memberId, Long participationId,
		CreateParticipationRequest request) {
		Participation participation = getParticipationOrThrow(participationId);

		participation.validateOwner(memberId);

		SurveyDetailDto surveyDetail = surveyPort.getSurveyDetail(authHeader, participation.getSurveyId());

		validateSurveyActive(surveyDetail);
		validateAllowUpdate(surveyDetail);

		List<ResponseData> responseDataList = request.getResponseDataList();
		List<SurveyDetailDto.QuestionValidationInfo> questions = surveyDetail.getQuestions();

		// 문항과 답변 유효성 검사
		validateQuestionsAndAnswers(responseDataList, questions);

		List<Response> responses = responseDataList.stream()
			.map(responseData -> Response.create(responseData.getQuestionId(), responseData.getAnswer()))
			.toList();

		UserSnapshotDto userSnapshot = userPort.getParticipantInfo(authHeader, memberId);
		ParticipantInfo participantInfo = ParticipantInfo.of(
			userSnapshot.getBirth(),
			userSnapshot.getGender(),
			userSnapshot.getRegion().getProvince(),
			userSnapshot.getRegion().getDistrict()
		);

		participation.update(responses, participantInfo);
	}

	@Transactional(readOnly = true)
	public Map<Long, Long> getCountsBySurveyIds(List<Long> surveyIds) {
		return participationRepository.countsBySurveyIds(surveyIds);
	}

	@Transactional(readOnly = true)
	public List<AnswerGroupResponse> getAnswers(List<Long> questionIds) {
		List<QuestionAnswer> questionAnswers = participationRepository.getAnswers(questionIds);

		Map<Long, List<QuestionAnswer>> listMap = questionAnswers.stream()
			.collect(Collectors.groupingBy(QuestionAnswer::getQuestionId));

		return questionIds.stream()
			.map(questionId -> {
				List<Map<String, Object>> answers = listMap.getOrDefault(questionId, Collections.emptyList()).stream()
					.map(QuestionAnswer::getAnswer)
					.toList();

				return AnswerGroupResponse.of(questionId, answers);
			})
			.toList();
	}

	/*
	private 메소드 정의
	 */
	private void validateParticipationDuplicated(Long surveyId, Long memberId) {
		if (participationRepository.exists(surveyId, memberId)) {
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

	private void validateQuestionsAndAnswers(
		List<ResponseData> responseDataList,
		List<SurveyDetailDto.QuestionValidationInfo> questions
	) {
		// 응답한 questionIds와 설문의 questionIds가 일치하는지 검증, answer = null 이여도 questionId는 존재해야 한다.
		validateQuestionIds(responseDataList, questions);

		Map<Long, SurveyDetailDto.QuestionValidationInfo> questionMap = questions.stream()
			.collect(Collectors.toMap(SurveyDetailDto.QuestionValidationInfo::getQuestionId, q -> q));

		for (ResponseData response : responseDataList) {
			Long questionId = response.getQuestionId();
			SurveyDetailDto.QuestionValidationInfo question = questionMap.get(questionId);
			Map<String, Object> answer = response.getAnswer();

			boolean validatedAnswerValue = validateAnswerValue(answer, question.getQuestionType());
			log.info("is_required: {}", question.isRequired());

			if (!validatedAnswerValue && !isEmpty(answer)) {
				log.info("INVALID_ANSWER_TYPE questionId : {}", questionId);
				throw new CustomException(CustomErrorCode.INVALID_ANSWER_TYPE);
			}

			if (question.isRequired() && (isEmpty(answer))) {
				log.info("REQUIRED_QUESTION_NOT_ANSWERED questionId : {}", questionId);
				throw new CustomException(CustomErrorCode.REQUIRED_QUESTION_NOT_ANSWERED);
			}

			// TODO: choice도 유효성 검사
		}
	}

	private void validateQuestionIds(
		List<ResponseData> responseDataList,
		List<SurveyDetailDto.QuestionValidationInfo> questions
	) {
		Set<Long> surveyQuestionIds = questions.stream()
			.map(SurveyDetailDto.QuestionValidationInfo::getQuestionId)
			.collect(Collectors.toSet());

		Set<Long> responseQuestionIds = responseDataList.stream()
			.map(ResponseData::getQuestionId)
			.collect(Collectors.toSet());

		if (!surveyQuestionIds.equals(responseQuestionIds)) {
			throw new CustomException(CustomErrorCode.INVALID_SURVEY_QUESTION);
		}
	}

	private boolean validateAnswerValue(Map<String, Object> answer, SurveyApiQuestionType questionType) {
		if (answer == null || answer.isEmpty()) {
			return true;
		}

		Object value = answer.values().iterator().next();
		if (value == null) {
			return true;
		}

		return switch (questionType) {
			case SINGLE_CHOICE -> answer.containsKey("choice") && value instanceof List;
			case MULTIPLE_CHOICE -> answer.containsKey("choices") && value instanceof List;
			case SHORT_ANSWER, LONG_ANSWER -> answer.containsKey("textAnswer") && value instanceof String;
			default -> false;
		};
	}

	private boolean isEmpty(Map<String, Object> answer) {
		if (answer == null || answer.isEmpty()) {
			return true;
		}
		Object value = answer.values().iterator().next();

		if (value == null) {
			return true;
		}
		if (value instanceof String) {
			return ((String)value).isBlank();
		}
		if (value instanceof List) {
			return ((List<?>)value).isEmpty();
		}

		return false;
	}
}
