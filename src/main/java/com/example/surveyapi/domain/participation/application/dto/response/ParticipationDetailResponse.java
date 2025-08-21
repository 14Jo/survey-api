package com.example.surveyapi.domain.participation.application.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationProjection;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationDetailResponse {

	private Long participationId;
	private LocalDateTime participatedAt;
	private List<AnswerDetail> responses;

	public static ParticipationDetailResponse fromEntity(Participation participation) {
		List<ParticipationDetailResponse.AnswerDetail> responses = participation.getAnswers()
			.stream()
			.map(AnswerDetail::from)
			.toList();

		ParticipationDetailResponse participationDetail = new ParticipationDetailResponse();
		participationDetail.participationId = participation.getId();
		participationDetail.participatedAt = participation.getUpdatedAt();
		participationDetail.responses = responses;

		return participationDetail;
	}

	public static ParticipationDetailResponse fromProjection(ParticipationProjection projection) {
		List<AnswerDetail> responses = projection.getResponses()
			.stream()
			.map(AnswerDetail::from)
			.toList();

		ParticipationDetailResponse participationDetail = new ParticipationDetailResponse();
		participationDetail.participationId = projection.getParticipationId();
		participationDetail.participatedAt = projection.getParticipatedAt();
		participationDetail.responses = responses;

		return participationDetail;

	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class AnswerDetail {

		private Long questionId;
		private Map<String, Object> answer;

		public static AnswerDetail from(ResponseData response) {
			AnswerDetail answerDetail = new AnswerDetail();
			answerDetail.questionId = response.getQuestionId();
			answerDetail.answer = response.getAnswer();

			return answerDetail;
		}
	}
}
