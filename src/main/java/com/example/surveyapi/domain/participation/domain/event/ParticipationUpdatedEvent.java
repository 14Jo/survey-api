package com.example.surveyapi.domain.participation.domain.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.response.Response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipationUpdatedEvent implements ParticipationEvent {

	private Long participationId;
	private Long surveyId;
	private Long userId;
	private LocalDateTime completedAt;
	private List<Answer> answers;

	public static ParticipationUpdatedEvent from(Participation participation) {
		ParticipationUpdatedEvent updatedEvent = new ParticipationUpdatedEvent();
		updatedEvent.participationId = participation.getId();
		updatedEvent.surveyId = participation.getSurveyId();
		updatedEvent.userId = participation.getUserId();
		updatedEvent.completedAt = participation.getUpdatedAt();
		updatedEvent.answers = Answer.from(participation.getResponses());

		return updatedEvent;
	}

	@Getter
	private static class Answer {

		private Long questionId;
		private List<Integer> choiceIds = new ArrayList<>();
		private String responseText;

		private static List<Answer> from(List<Response> responses) {
			return responses.stream()
				.map(response -> {
					Answer answerDto = new Answer();
					answerDto.questionId = response.getQuestionId();

					Map<String, Object> rawAnswer = response.getAnswer();

					if (rawAnswer != null && !rawAnswer.isEmpty()) {
						Object value = rawAnswer.values().iterator().next();

						if (value instanceof String) {
							answerDto.responseText = (String)value;
						} else if (value instanceof List<?> rawList) {
							answerDto.choiceIds = rawList.stream()
								.filter(Integer.class::isInstance)
								.map(Integer.class::cast)
								.collect(Collectors.toList());
						}
					}
					return answerDto;
				})
				.collect(Collectors.toList());
		}
	}
}
