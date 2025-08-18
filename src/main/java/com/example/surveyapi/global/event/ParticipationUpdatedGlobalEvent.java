package com.example.surveyapi.global.event;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.global.model.ParticipationGlobalEvent;

import lombok.Getter;

@Getter
public class ParticipationUpdatedGlobalEvent implements ParticipationGlobalEvent {

	private final Long participationId;
	private final Long surveyId;
	private final Long userId;
	private final LocalDateTime completedAt;
	private final List<Answer> answers;

	public ParticipationUpdatedGlobalEvent(Long participationId, Long surveyId, Long userId,
		LocalDateTime completedAt, List<Answer> answers) {
		this.participationId = participationId;
		this.surveyId = surveyId;
		this.userId = userId;
		this.completedAt = completedAt;
		this.answers = answers;
	}

	@Getter
	private static class Answer {

		private final Long questionId;
		private final List<Integer> choiceIds;
		private final String responseText;

		public Answer(Long questionId, List<Integer> choiceIds, String responseText) {
			this.questionId = questionId;
			this.choiceIds = choiceIds;
			this.responseText = responseText;
		}
	}
}
