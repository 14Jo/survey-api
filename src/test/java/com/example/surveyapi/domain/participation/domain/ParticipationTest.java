package com.example.surveyapi.domain.participation.domain;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
		Participation participation = Participation.create(memberId, surveyId, participantInfo);

		// then
		assertThat(participation.getMemberId()).isEqualTo(memberId);
		assertThat(participation.getSurveyId()).isEqualTo(surveyId);
		assertThat(participation.getParticipantInfo()).isEqualTo(participantInfo);
	}

	@Test
	@DisplayName("응답 추가")
	void addResponse() {
		// given
		Participation participation = Participation.create(1L, 1L, new ParticipantInfo());
		Response response = Response.create(1L, Map.of("textAnswer", "주관식 및 서술형"));

		// when
		participation.addResponse(response);

		// then
		assertThat(participation.getResponses()).hasSize(1);
		assertThat(participation.getResponses().get(0)).isEqualTo(response);
		assertThat(response.getParticipation()).isEqualTo(participation);
	}

	@Test
	@DisplayName("참여 기록 본인 검증 성공")
	void validateOwner_notThrowException() {
		// given
		Long ownerId = 1L;
		Participation participation = Participation.create(ownerId, 1L, new ParticipantInfo());

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
		Participation participation = Participation.create(ownerId, 1L, new ParticipantInfo());

		// when & then
		assertThatThrownBy(() -> participation.validateOwner(otherId))
			.isInstanceOf(CustomException.class)
			.hasMessage(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW.getMessage());
	}
}
