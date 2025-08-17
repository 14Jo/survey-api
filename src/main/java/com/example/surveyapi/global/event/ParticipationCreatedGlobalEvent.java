package com.example.surveyapi.global.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.participation.domain.participation.enums.Gender;
import com.example.surveyapi.domain.participation.domain.participation.vo.Region;
import com.example.surveyapi.global.model.ParticipationGlobalEvent;

import lombok.Getter;

@Getter
public class ParticipationCreatedGlobalEvent implements ParticipationGlobalEvent {

	private final Long participationId;
	private final Long surveyId;
	private final Long userId;
	private final ParticipantInfoDto demographic;
	private final LocalDateTime completedAt;
	private final List<Answer> answers;

	public ParticipationCreatedGlobalEvent(Long participationId, Long surveyId, Long userId,
		ParticipantInfoDto demographic,
		LocalDateTime completedAt, List<Answer> answers) {
		this.participationId = participationId;
		this.surveyId = surveyId;
		this.userId = userId;
		this.demographic = demographic;
		this.completedAt = completedAt;
		this.answers = answers;
	}

	@Getter
	public static class ParticipantInfoDto {

		private final LocalDate birth;
		private final Gender gender;
		private final Region region;

		public ParticipantInfoDto(LocalDate birth, Gender gender, Region region) {
			this.birth = birth;
			this.gender = gender;
			this.region = region;
		}
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
