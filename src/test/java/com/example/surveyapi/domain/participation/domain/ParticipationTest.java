package com.example.surveyapi.domain.participation.domain;

class ParticipationTest {

	// @Test
	// @DisplayName("참여 생성")
	// void createParticipation() {
	// 	// given
	// 	Long memberId = 1L;
	// 	Long surveyId = 1L;
	// 	ParticipantInfo participantInfo = new ParticipantInfo();
	//
	// 	// when
	// 	Participation participation = Participation.create(memberId, surveyId, participantInfo);
	//
	// 	// then
	// 	assertThat(participation.getMemberId()).isEqualTo(memberId);
	// 	assertThat(participation.getSurveyId()).isEqualTo(surveyId);
	// 	assertThat(participation.getParticipantInfo()).isEqualTo(participantInfo);
	// }
	//
	// @Test
	// @DisplayName("응답 추가")
	// void addResponse() {
	// 	// given
	// 	Participation participation = Participation.create(1L, 1L, new ParticipantInfo());
	// 	Response response = Response.create(1L, Map.of("textAnswer", "주관식 및 서술형"));
	//
	// 	// when
	// 	participation.addResponse(response);
	//
	// 	// then
	// 	assertThat(participation.getResponses()).hasSize(1);
	// 	assertThat(participation.getResponses().get(0)).isEqualTo(response);
	// 	assertThat(response.getParticipation()).isEqualTo(participation);
	// }
}
