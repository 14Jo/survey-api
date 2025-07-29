package com.example.surveyapi.domain.participation.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.participation.domain.response.Response;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ParticipationService {

	private final ParticipationRepository participationRepository;

	@Transactional
	public Long create(Long surveyId, Long memberId, CreateParticipationRequest request) {
		if (participationRepository.exists(surveyId, memberId)) {
			throw new CustomException(CustomErrorCode.SURVEY_ALREADY_PARTICIPATED);
		}
		// TODO: 설문 유효성 검증 요청
		// TODO: memberId가 설문의 대상이 맞는지 공유에 검증 요청
		List<ResponseData> responseDataList = request.getResponseDataList();

		// TODO: 멤버의 participantInfo 스냅샷 설정을 위해 Member에 요청, REST 통신으로 받아온 json 데이터를 dto로 받을지 고려하고
		// TODO: participantInfo를 도메인 create 에서 생성하도록 수정
		ParticipantInfo participantInfo = new ParticipantInfo();
		Participation participation = Participation.create(memberId, surveyId, participantInfo, responseDataList);

		Participation savedParticipation = participationRepository.save(participation);
		//TODO: 설문의 중복 참여는 어디서 검증해야하는지 확인

		return savedParticipation.getId();
	}

	@Transactional(readOnly = true)
	public Page<ParticipationInfoResponse> gets(Long memberId, Pageable pageable) {
		Page<ParticipationInfo> participationInfos = participationRepository.findParticipationsInfo(memberId,
			pageable);

		List<Long> surveyIds = participationInfos.getContent().stream()
			.map(ParticipationInfo::getSurveyId)
			.toList();

		// TODO: List<Long> surveyIds를 매개변수로 id, 설문 제목, 설문 기한, 설문 상태(진행중인지 종료인지), 수정이 가능한 설문인지 요청
		List<ParticipationInfoResponse.SurveyInfoOfParticipation> surveyInfoOfParticipations = new ArrayList<>();

		// 임시 더미데이터 생성
		for (Long surveyId : surveyIds) {
			surveyInfoOfParticipations.add(
				ParticipationInfoResponse.SurveyInfoOfParticipation.of(surveyId, "설문 제목" + surveyId, "진행 중",
					LocalDate.now().plusWeeks(1),
					true));
		}

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
		Participation participation = participationRepository.findById(participationId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION));

		participation.validateOwner(loginMemberId);

		return ParticipationDetailResponse.from(participation);
	}

	@Transactional
	public void update(Long loginMemberId, Long participationId, CreateParticipationRequest request) {
		Participation participation = participationRepository.findById(participationId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION));

		participation.validateOwner(loginMemberId);

		List<Response> responses = request.getResponseDataList().stream()
			.map(responseData -> Response.create(responseData.getQuestionId(), responseData.getAnswer()))
			.toList();

		participation.update(responses);
	}
}
