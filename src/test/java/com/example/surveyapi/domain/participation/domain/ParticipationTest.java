package com.example.surveyapi.domain.participation.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.participation.domain.response.Response;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

class ParticipationTest {

	@Test
	@DisplayName("참여 생성")
	void createParticipation() {
		// given
		Long memberId = 1L;
		Long surveyId = 1L;
		ParticipantInfo participantInfo = new ParticipantInfo();

		// when
		Participation participation = Participation.create(memberId, surveyId, participantInfo,
			Collections.emptyList());

		// then
		assertThat(participation.getMemberId()).isEqualTo(memberId);
		assertThat(participation.getSurveyId()).isEqualTo(surveyId);
		assertThat(participation.getParticipantInfo()).isEqualTo(participantInfo);
	}

	@Test
	@DisplayName("응답이 추가된 참여 생성")
	void addResponse() {
		// given
		Long memberId = 1L;
		Long surveyId = 1L;

		ResponseData responseData1 = new ResponseData();
		ReflectionTestUtils.setField(responseData1, "questionId", 1L);
		ReflectionTestUtils.setField(responseData1, "answer", Map.of("textAnswer", "주관식 및 서술형 답변입니다."));

		ResponseData responseData2 = new ResponseData();
		ReflectionTestUtils.setField(responseData2, "questionId", 2L);
		ReflectionTestUtils.setField(responseData2, "answer", Map.of("choice", "2"));

		List<ResponseData> responseDataList = List.of(responseData1, responseData2);

		// when
		Participation participation = Participation.create(memberId, surveyId, new ParticipantInfo(), responseDataList);

		// then
		assertThat(participation).isNotNull();
		assertThat(participation.getSurveyId()).isEqualTo(surveyId);
		assertThat(participation.getMemberId()).isEqualTo(memberId);
		assertThat(participation.getParticipantInfo()).isEqualTo(new ParticipantInfo());

		assertThat(participation.getResponses()).hasSize(2);
		Response createdResponse1 = participation.getResponses().get(0);
		assertThat(createdResponse1.getQuestionId()).isEqualTo(responseData1.getQuestionId());
		assertThat(createdResponse1.getAnswer()).isEqualTo(responseData1.getAnswer());
		assertThat(createdResponse1.getParticipation()).isEqualTo(participation);

		Response createdResponse2 = participation.getResponses().get(1);
		assertThat(createdResponse2.getQuestionId()).isEqualTo(responseData2.getQuestionId());
		assertThat(createdResponse2.getAnswer()).isEqualTo(responseData2.getAnswer());
		assertThat(createdResponse2.getParticipation()).isEqualTo(participation);
	}

	@Test
	@DisplayName("참여 기록 본인 검증 성공")
	void validateOwner_notThrowException() {
		// given
		Long ownerId = 1L;
		Participation participation = Participation.create(ownerId, 1L, new ParticipantInfo(), Collections.emptyList());

		// when & then
		assertThatCode(() -> participation.validateOwner(ownerId))
			.doesNotThrowAnyException();
	}

	@Test
	@DisplayName("참여 기록 본인 검증 실패")
	void validateOwner_throwException() {
		// given
		Long ownerId = 1L;
		Long otherId = 2L;
		Participation participation = Participation.create(ownerId, 1L, new ParticipantInfo(), Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> participation.validateOwner(otherId))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage());
	}

	@Test
	@DisplayName("참여 기록 수정")
	void updateParticipation() {
		// given
		Long memberId = 1L;
		Long surveyId = 1L;
		ParticipantInfo participantInfo = new ParticipantInfo();

		ResponseData ResponseData1 = new ResponseData();
		ReflectionTestUtils.setField(ResponseData1, "questionId", 1L);
		ReflectionTestUtils.setField(ResponseData1, "answer", Map.of("textAnswer", "초기 답변1"));

		ResponseData ResponseData2 = new ResponseData();
		ReflectionTestUtils.setField(ResponseData2, "questionId", 2L);
		ReflectionTestUtils.setField(ResponseData2, "answer", Map.of("choice", 3));

		List<ResponseData> initialResponseDataList = List.of(ResponseData1, ResponseData2);
		Participation participation = Participation.create(memberId, surveyId, participantInfo,
			initialResponseDataList);

		Response newResponse1 = Response.create(1L, Map.of("textAnswer", "수정된 답변1"));
		Response newResponse2 = Response.create(2L, Map.of("choice", "4"));

		List<Response> newResponses = List.of(newResponse1, newResponse2);

		// when
		participation.update(newResponses);

		// then
		assertThat(participation.getResponses()).hasSize(2);
		assertThat(participation.getResponses())
			.extracting("questionId")
			.containsExactlyInAnyOrder(1L, 2L);

		Response updatedResponse1 = participation.getResponses().stream()
			.filter(r -> r.getQuestionId().equals(1L))
			.findFirst().orElseThrow();
		assertThat(updatedResponse1.getAnswer()).isEqualTo(Map.of("textAnswer", "수정된 답변1"));

		Response updatedResponse2 = participation.getResponses().stream()
			.filter(r -> r.getQuestionId().equals(2L))
			.findFirst().orElseThrow();
		assertThat(updatedResponse2.getAnswer()).isEqualTo(Map.of("choice", "4"));
	}
}
