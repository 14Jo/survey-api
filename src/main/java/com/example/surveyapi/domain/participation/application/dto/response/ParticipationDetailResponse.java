package com.example.surveyapi.domain.participation.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.response.Response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationDetailResponse {

	private Long participationId;
	private LocalDateTime participatedAt;
	private List<AnswerDetail> responses;

	public static ParticipationDetailResponse from(Participation participation) {
		List<ParticipationDetailResponse.AnswerDetail> responses = participation.getResponses()
			.stream()
			.map(AnswerDetail::from)
			.toList();

		ParticipationDetailResponse participationDetail = new ParticipationDetailResponse();
		participationDetail.participationId = participation.getId();
		participationDetail.participatedAt = participation.getUpdatedAt();
		participationDetail.responses = responses;

		return participationDetail;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AnswerDetail {

		private Long questionId;
		private Map<String, Object> answer;

		public static AnswerDetail from(Response response) {
			AnswerDetail answerDetail = new AnswerDetail();
			answerDetail.questionId = response.getQuestionId();
			answerDetail.answer = response.getAnswer();

			return answerDetail;
		}
	}
}
