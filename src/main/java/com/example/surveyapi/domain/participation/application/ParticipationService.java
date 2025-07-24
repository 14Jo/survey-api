package com.example.surveyapi.domain.participation.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.request.ResponseData;
import com.example.surveyapi.domain.participation.application.dto.request.SurveyInfoOfParticipation;
import com.example.surveyapi.domain.participation.application.dto.response.ReadParticipationPageResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ReadParticipationResponse;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
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
		// TODO: 설문 유효성 검증 요청
		// TODO: memberId가 설문의 대상이 맞는지 공유에 검증 요청
		List<ResponseData> responseDataList = request.getResponseDataList();

		// TODO: 멤버의 participantInfo 스냅샷 설정을 위해 Member에 요청, REST 통신으로 받아온 json 데이터를 dto로 받을지 고려하고
		// TODO: participantInfo를 도메인 create 에서 생성하도록 수정
		ParticipantInfo participantInfo = new ParticipantInfo();
		Participation participation = Participation.create(memberId, surveyId, participantInfo);

		for (ResponseData responseData : responseDataList) {
			// TODO: questionId가 해당 survey에 속하는지(보류), 받아온 questionType으로 answer의 key값이 올바른지 유효성 검증
			Response response = Response.create(responseData.getQuestionId(), responseData.getAnswer());

			participation.addResponse(response);
		}

		Participation savedParticipation = participationRepository.save(participation);
		//TODO: 설문의 중복 참여는 어디서 검증해야하는지 확인

		return savedParticipation.getId();
	}

	@Transactional(readOnly = true)
	public Page<ReadParticipationPageResponse> gets(Long memberId, Pageable pageable) {
		Page<Participation> participations = participationRepository.findAll(memberId, pageable);

		List<Long> surveyIds = participations.get().map(Participation::getSurveyId).distinct().toList();

		// TODO: List<Long> surveyIds를 매개변수로 id, 설문 제목, 설문 기한, 설문 상태(진행중인지 종료인지), 수정이 가능한 설문인지 요청
		List<SurveyInfoOfParticipation> surveyInfoOfParticipations = new ArrayList<>();

		// 더미데이터 생성
		for (Long surveyId : surveyIds) {
			surveyInfoOfParticipations.add(
				new SurveyInfoOfParticipation(surveyId, "설문 제목" + surveyId, "진행 중", LocalDate.now().plusWeeks(1),
					true));
		}

		Map<Long, SurveyInfoOfParticipation> surveyInfoMap = surveyInfoOfParticipations.stream()
			.collect(Collectors.toMap(
				SurveyInfoOfParticipation::getSurveyId,
				surveyInfo -> surveyInfo
			));

		// TODO: stream 한번만 사용하여서 map 수정
		return participations.map(p -> {
			SurveyInfoOfParticipation surveyInfo = surveyInfoMap.get(p.getSurveyId());

			return ReadParticipationPageResponse.of(p, surveyInfo);
		});
	}

	@Transactional(readOnly = true)
	public ReadParticipationResponse get(Long loginMemberId, Long participationId) {
		Participation participation = participationRepository.findById(participationId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION));

		validateOwner(participation.getMemberId(), loginMemberId);

		List<ReadParticipationResponse.AnswerDetail> answerDetails = participation.getResponses()
			.stream()
			.map(r -> new ReadParticipationResponse.AnswerDetail(r.getQuestionId(), r.getAnswer()))
			.toList();

		return new ReadParticipationResponse(participationId, answerDetails);
	}

	/*
	 private 메소드
	 */
	private void validateOwner(Long participationMemberId, Long loginMemberId) {
		if (!participationMemberId.equals(loginMemberId)) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW);
		}
	}
}

