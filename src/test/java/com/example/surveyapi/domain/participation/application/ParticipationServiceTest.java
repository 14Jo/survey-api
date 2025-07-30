package com.example.surveyapi.domain.participation.application;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.participation.application.dto.request.CreateParticipationRequest;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.domain.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@TestPropertySource(properties = "SECRET_KEY=SecretKeyExample42534D@DAF!1243zvjnjw@")
@SpringBootTest
@Transactional
class ParticipationServiceTest {

	@Autowired
	private ParticipationService participationService;

	@Autowired
	private ParticipationRepository participationRepository;

	private ResponseData createResponseData(Long questionId, Map<String, Object> answer) {
		ResponseData responseData = new ResponseData();
		ReflectionTestUtils.setField(responseData, "questionId", questionId);
		ReflectionTestUtils.setField(responseData, "answer", answer);

		return responseData;
	}

	@Test
	@DisplayName("설문 응답 제출")
	void createParticipationAndResponses() {
		// given
		Long surveyId = 1L;
		Long memberId = 1L;

		List<ResponseData> responseDataList = List.of(
			createResponseData(1L, Map.of("textAnswer", "주관식 및 서술형")),
			createResponseData(2L, Map.of("choices", List.of(1, 3)))
		);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", responseDataList);

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

	@Test
	@DisplayName("나의 전체 참여 목록 조회")
	void getAllMyParticipation() {
		// given
		Long myMemberId = 1L;
		participationRepository.save(
			Participation.create(myMemberId, 1L, new ParticipantInfo(), Collections.emptyList()));
		participationRepository.save(
			Participation.create(myMemberId, 3L, new ParticipantInfo(), Collections.emptyList()));
		participationRepository.save(
			Participation.create(2L, 1L, new ParticipantInfo(), Collections.emptyList()));

		Pageable pageable = PageRequest.of(0, 5);

		// when
		Page<ParticipationInfoResponse> result = participationService.gets(myMemberId, pageable);

		// then
		assertThat(result.getTotalElements()).isEqualTo(2);
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getContent().get(0).getSurveyInfo().getSurveyId()).isEqualTo(1L);
		assertThat(result.getContent().get(1).getSurveyInfo().getSurveyId()).isEqualTo(3L);
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회")
	void getParticipation() {
		// given
		Long memberId = 1L;
		Long surveyId = 1L;
		Participation savedParticipation = participationRepository.save(
			Participation.create(memberId, surveyId, new ParticipantInfo(),
				List.of(createResponseData(1L, Map.of("textAnswer", "상세 조회 답변")))));

		// when
		ParticipationDetailResponse result = participationService.get(memberId, savedParticipation.getId());

		// then
		assertThat(result).isNotNull();
		assertThat(result.getParticipationId()).isEqualTo(savedParticipation.getId());
		assertThat(result.getResponses()).hasSize(1);
		assertThat(result.getResponses().get(0).getQuestionId()).isEqualTo(1L);
		assertThat(result.getResponses().get(0).getAnswer()).isEqualTo(Map.of("textAnswer", "상세 조회 답변"));
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회 실패 - 참여 기록 없음")
	void getParticipation_notFound() {
		// given
		Long memberId = 1L;
		Long notExistParticipationId = 999L;

		// when & then
		assertThatThrownBy(() -> participationService.get(memberId, notExistParticipationId))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.NOT_FOUND_PARTICIPATION.getMessage());
	}

	@Test
	@DisplayName("나의 참여 응답 상세 조회 실패 - 접근 권한 없음")
	void getParticipation_accessDenied() {
		// given
		Long ownerId = 1L;
		Long otherId = 2L;
		Participation participation = Participation.create(ownerId, 1L, new ParticipantInfo(),
			List.of(createResponseData(1L, Map.of("textAnswer", "초기 답변"))));
		Participation savedParticipation = participationRepository.save(participation);

		// when & then
		assertThatThrownBy(() -> participationService.get(otherId, savedParticipation.getId()))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage());
	}

	@Test
	@DisplayName("참여 응답 수정")
	void updateParticipation() {
		// given
		Long memberId = 1L;
		Long surveyId = 1L;
		Participation participation = Participation.create(memberId, surveyId, new ParticipantInfo(),
			List.of(createResponseData(1L, Map.of("textAnswer", "초기 답변"))));
		Participation savedParticipation = participationRepository.save(participation);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList",
			List.of(createResponseData(1L, Map.of("textAnswer", "수정된 답변"))));

		// when
		participationService.update(memberId, savedParticipation.getId(), request);

		// then
		Participation updatedParticipation = participationRepository.findById(savedParticipation.getId()).orElseThrow();
		assertThat(updatedParticipation.getResponses()).hasSize(1);
		assertThat(updatedParticipation.getResponses().get(0).getAnswer()).isEqualTo(Map.of("textAnswer", "수정된 답변"));
	}

	@Test
	@DisplayName("참여 응답 수정 실패 - 참여 기록 없음")
	void updateParticipation_notFound() {
		// given
		Long memberId = 1L;
		Long notExistParticipationId = 999L;
		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> participationService.update(memberId, notExistParticipationId, request))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.NOT_FOUND_PARTICIPATION.getMessage());
	}

	@Test
	@DisplayName("참여 응답 수정 실패 - 접근 권한 없음")
	void updateParticipation_accessDenied() {
		// given
		Long ownerId = 1L;
		Long otherId = 2L;

		Participation participation = Participation.create(ownerId, 1L, new ParticipantInfo(),
			List.of(createResponseData(1L, Map.of("textAnswer", "초기 답변"))));
		Participation savedParticipation = participationRepository.save(participation);

		CreateParticipationRequest request = new CreateParticipationRequest();
		ReflectionTestUtils.setField(request, "responseDataList", Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> participationService.update(otherId, savedParticipation.getId(), request))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage());
	}

	@Test
	@DisplayName("여러 설문에 대한 모든 참여 응답 기록 조회")
	void getAllBySurveyIds() {
		// given
		Long memberId = 1L;
		Long surveyId1 = 10L;
		Long surveyId2 = 20L;

		participationRepository.save(Participation.create(memberId, surveyId1, new ParticipantInfo(), List.of(
			createResponseData(1L, Map.of("textAnswer", "답변1-1"))
		)));
		participationRepository.save(Participation.create(memberId, surveyId1, new ParticipantInfo(), List.of(
			createResponseData(2L, Map.of("textAnswer", "답변1-2"))
		)));

		participationRepository.save(Participation.create(memberId, surveyId2, new ParticipantInfo(), List.of(
			createResponseData(3L, Map.of("textAnswer", "답변2"))
		)));

		List<Long> SurveyIds = List.of(surveyId1, surveyId2);

		// when
		List<ParticipationGroupResponse> result = participationService.getAllBySurveyIds(SurveyIds);

		// then
		assertThat(result).hasSize(2);

		ParticipationGroupResponse group1 = result.stream()
			.filter(g -> g.getSurveyId().equals(surveyId1))
			.findFirst().orElseThrow();
		assertThat(group1.getParticipations()).hasSize(2);
		assertThat(group1.getParticipations())
			.extracting(p -> p.getResponses().get(0).getAnswer())
			.containsExactlyInAnyOrder(Map.of("textAnswer", "답변1-1"), Map.of("textAnswer", "답변1-2"));

		ParticipationGroupResponse group2 = result.stream()
			.filter(g -> g.getSurveyId().equals(surveyId2))
			.findFirst().orElseThrow();
		assertThat(group2.getParticipations()).hasSize(1);
		assertThat(group2.getParticipations())
			.extracting(p -> p.getResponses().get(0).getAnswer())
			.containsExactly(Map.of("textAnswer", "답변2"));
	}
}
