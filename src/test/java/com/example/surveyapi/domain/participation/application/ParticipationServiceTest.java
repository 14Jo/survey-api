package com.example.surveyapi.domain.participation.application;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.request.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;

@TestPropertySource(properties = "SECRET_KEY=SecretKeyExample42534D@DAF!1243zvjnjw@")
@SpringBootTest
@Transactional
class ParticipationServiceTest {

	@Autowired
	private ParticipationService participationService;

	@Autowired
	private ParticipationRepository participationRepository;

	@Test
	@DisplayName("설문 응답 제출 성공")
	void createParticipationAndResponses() {
		// given
		Long surveyId = 1L;
		Long memberId = 1L;

		ResponseData responseData1 = new ResponseData(1L, Map.of("textAnswer", "주관식 및 서술형"));
		ResponseData responseData2 = new ResponseData(2L, Map.of("choices", List.of(1, 3)));
		List<ResponseData> responseDataList = new ArrayList<>(List.of(responseData1, responseData2));

		CreateParticipationRequest request = new CreateParticipationRequest(responseDataList);

		// when
		Long participationId = participationService.create(surveyId, memberId, request);

		// then
		Optional<Participation> savedParticipation = participationRepository.findById(participationId);

		assertThat(savedParticipation).isPresent();

		Participation participation = savedParticipation.get();

		assertThat(participation.getMemberId()).isEqualTo(memberId);
		assertThat(participation.getSurveyId()).isEqualTo(surveyId);
		assertThat(participation.getResponses()).hasSize(2);

		assertThat(participation.getResponses())
			.extracting("questionId")
			.containsExactlyInAnyOrder(1L, 2L);

		assertThat(participation.getResponses())
			.extracting("answer")
			.containsExactlyInAnyOrder(
				Map.of("textAnswer", "주관식 및 서술형"),
				Map.of("choices", List.of(1, 3))
			);
	}
}