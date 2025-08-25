package com.example.surveyapi.participation.domain.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.surveyapi.participation.domain.command.ResponseData;
import com.example.surveyapi.participation.domain.participation.Participation;
import com.example.surveyapi.participation.domain.participation.vo.ParticipantInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipationCreatedEvent implements ParticipationEvent {

	private Long participationId;
	private Long surveyId;
	private Long userId;
	private ParticipantInfo demographic;
	private LocalDateTime completedAt;
	private List<Answer> answers;

	public static ParticipationCreatedEvent from(Participation participation) {
		ParticipationCreatedEvent createdEvent = new ParticipationCreatedEvent();
		createdEvent.participationId = participation.getId();
		createdEvent.surveyId = participation.getSurveyId();
		createdEvent.userId = participation.getUserId();
		createdEvent.demographic = participation.getParticipantInfo();
		createdEvent.completedAt = participation.getUpdatedAt();
		createdEvent.answers = Answer.from(participation.getAnswers());

		return createdEvent;
	}

	@Getter
	private static class Answer {

		private Long questionId;
		private List<Integer> choiceIds = new ArrayList<>();
		private String responseText;

		private static List<Answer> from(List<ResponseData> responses) {
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
