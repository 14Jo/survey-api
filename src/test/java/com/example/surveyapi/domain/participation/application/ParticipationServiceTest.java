package com.example.surveyapi.domain.participation.application;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.request.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

	@InjectMocks
	private ParticipationService participationService;

	@Mock
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

		Participation savedParticipation = Participation.create(memberId, surveyId, null);
		ReflectionTestUtils.setField(savedParticipation, "id", 1L);

		when(participationRepository.save(any(Participation.class))).thenReturn(savedParticipation);

		// when
		Long participationId = participationService.create(surveyId, memberId, request);

		// then
		assertThat(participationId).isEqualTo(1L);
		assertThat(savedParticipation.getMemberId()).isEqualTo(memberId);
		assertThat(savedParticipation.getSurveyId()).isEqualTo(surveyId);
		assertThat(savedParticipation.getResponses()).hasSize(2);

		assertThat(savedParticipation.getResponses())
			.extracting("questionId")
			.containsExactlyInAnyOrder(1L, 2L);

		assertThat(savedParticipation.getResponses())
			.extracting("answer")
			.containsExactlyInAnyOrder(
				Map.of("textAnswer", "주관식 및 서술형"),
				Map.of("choices", List.of(1, 3))
			);
	}
}
