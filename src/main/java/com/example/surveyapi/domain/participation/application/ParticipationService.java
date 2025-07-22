package com.example.surveyapi.domain.participation.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.request.ResponseData;
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

		// TODO: 멤버의 participantInfo 스냅샷 설정을 위해 Member에 요청
		ParticipantInfo participantInfo = new ParticipantInfo();

		Participation participation = Participation.create(memberId, surveyId, participantInfo);

		for (ResponseData responseData : responseDataList) {
			Response response = Response.create(responseData.getQuestionId(), responseData.getQuestionType(),
				responseData.getAnswer());

			participation.addResponse(response);
		}

		Participation savedParticipation = participationRepository.save(participation);
		//TODO: 설문의 중복 참여는 어디서 검증해야하는지 확인

		return savedParticipation.getId();
	}
}
