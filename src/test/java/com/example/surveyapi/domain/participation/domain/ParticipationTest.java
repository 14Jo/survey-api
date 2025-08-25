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
import com.example.surveyapi.domain.participation.domain.participation.enums.Gender;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.participation.domain.participation.vo.Region;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

class ParticipationTest {

	private ResponseData createResponseData(Long questionId, Map<String, Object> answer) {
		ResponseData responseData = new ResponseData();
		ReflectionTestUtils.setField(responseData, "questionId", questionId);
		ReflectionTestUtils.setField(responseData, "answer", answer);
		return responseData;
	}

	@Test
	@DisplayName("참여 생성")
	void createParticipation() {
		// given
		Long userId = 1L;
		Long surveyId = 1L;
		ParticipantInfo participantInfo = ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE,
			Region.of("서울", "강남구"));

		// when
		Participation participation = Participation.create(userId, surveyId, participantInfo,
			Collections.emptyList());

		// then
		assertThat(participation.getUserId()).isEqualTo(userId);
		assertThat(participation.getSurveyId()).isEqualTo(surveyId);
		assertThat(participation.getParticipantInfo()).isEqualTo(participantInfo);
	}

	@Test
	@DisplayName("응답이 추가된 참여 생성")
	void addResponse() {
		// given
		Long userId = 1L;
		Long surveyId = 1L;

		ResponseData responseData1 = createResponseData(1L, Map.of("textAnswer", "주관식 및 서술형 답변입니다."));
		ResponseData responseData2 = createResponseData(2L, Map.of("choice", "2"));

		List<ResponseData> responseDataList = List.of(responseData1, responseData2);

		// when
		Participation participation = Participation.create(userId, surveyId,
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, Region.of("서울", "강남구")), responseDataList);

		// then
		assertThat(participation).isNotNull();
		assertThat(participation.getSurveyId()).isEqualTo(surveyId);
		assertThat(participation.getUserId()).isEqualTo(userId);
		assertThat(participation.getParticipantInfo()).isEqualTo(
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, Region.of("서울", "강남구")));

		assertThat(participation.getAnswers()).hasSize(2);
		ResponseData createdResponse1 = participation.getAnswers().get(0);
		assertThat(createdResponse1.getQuestionId()).isEqualTo(responseData1.getQuestionId());
		assertThat(createdResponse1.getAnswer()).isEqualTo(responseData1.getAnswer());

		ResponseData createdResponse2 = participation.getAnswers().get(1);
		assertThat(createdResponse2.getQuestionId()).isEqualTo(responseData2.getQuestionId());
		assertThat(createdResponse2.getAnswer()).isEqualTo(responseData2.getAnswer());
	}

	@Test
	@DisplayName("참여 기록 본인 검증 성공")
	void validateOwner_notThrowException() {
		// given
		Long ownerId = 1L;
		Participation participation = Participation.create(ownerId, 1L,
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, Region.of("서울", "강남구")),
			Collections.emptyList());

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
		Participation participation = Participation.create(ownerId, 1L,
			ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE, Region.of("서울", "강남구")),
			Collections.emptyList());

		// when & then
		assertThatThrownBy(() -> participation.validateOwner(otherId))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage());
	}

	@Test
	@DisplayName("참여 기록 수정")
	void updateParticipation() {
		// given
		Long userId = 1L;
		Long surveyId = 1L;
		ParticipantInfo participantInfo = ParticipantInfo.of("2000-01-01T00:00:00", Gender.MALE,
			Region.of("서울", "강남구"));

		ResponseData responseData1 = createResponseData(1L, Map.of("textAnswer", "초기 답변1"));
		ResponseData responseData2 = createResponseData(2L, Map.of("choice", 3));

		List<ResponseData> initialResponseDataList = List.of(responseData1, responseData2);
		Participation participation = Participation.create(userId, surveyId, participantInfo,
			initialResponseDataList);

		ResponseData newResponseData1 = createResponseData(1L, Map.of("textAnswer", "수정된 답변1"));
		ResponseData newResponseData2 = createResponseData(2L, Map.of("choice", "4"));

		List<ResponseData> newResponseDataList = List.of(newResponseData1, newResponseData2);

		// when
		participation.update(newResponseDataList);

		// then
		assertThat(participation.getAnswers()).hasSize(2);
		assertThat(participation.getAnswers())
			.extracting("questionId")
			.containsExactlyInAnyOrder(1L, 2L);

		ResponseData updatedResponse1 = participation.getAnswers().stream()
			.filter(r -> r.getQuestionId().equals(1L))
			.findFirst().orElseThrow();
		assertThat(updatedResponse1.getAnswer()).isEqualTo(Map.of("textAnswer", "수정된 답변1"));

		ResponseData updatedResponse2 = participation.getAnswers().stream()
			.filter(r -> r.getQuestionId().equals(2L))
			.findFirst().orElseThrow();
		assertThat(updatedResponse2.getAnswer()).isEqualTo(Map.of("choice", "4"));
	}
}