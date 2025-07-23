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
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.participation.domain.response.Response;

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

		List<Long> surveyIds = participations.get().map(Participation::getSurveyId).toList();

		List<SurveyInfoOfParticipation> surveyInfoOfParticipations = new ArrayList<>();
		surveyInfoOfParticipations.add(
			new SurveyInfoOfParticipation(1L, "설문 제목", "진행 중", LocalDate.now().plusWeeks(1), true));

		Map<Long, SurveyInfoOfParticipation> surveyInfoMap = surveyInfoOfParticipations.stream()
			.collect(Collectors.toMap(
				SurveyInfoOfParticipation::getSurveyId,
				surveyInfo -> surveyInfo
			));

		return participations.map(p -> {
			SurveyInfoOfParticipation surveyInfo = surveyInfoMap.get(p.getSurveyId());

			return ReadParticipationPageResponse.of(p, surveyInfo);
		});
	}
}
