package com.example.surveyapi.domain.participation.application.dto.response;

import java.util.List;
import java.util.Map;

import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.response.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadParticipationResponse {

	private Long participationId;
	private List<AnswerDetail> responses;

	public static ReadParticipationResponse from(Participation participation) {
		List<ReadParticipationResponse.AnswerDetail> answerDetails = participation.getResponses()
			.stream()
			.map(AnswerDetail::from)
			.toList();

		return new ReadParticipationResponse(participation.getId(), answerDetails);
	}

	@Getter
	@AllArgsConstructor
	public static class AnswerDetail {
		private Long questionId;
		private Map<String, Object> answer;

		public static AnswerDetail from(Response response) {
			return new AnswerDetail(response.getQuestionId(), response.getAnswer());
		}
	}
}
