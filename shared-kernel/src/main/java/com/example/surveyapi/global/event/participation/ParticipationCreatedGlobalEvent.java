package com.example.surveyapi.global.event.participation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
		private final String gender;
		private final RegionDto region;

		public ParticipantInfoDto(LocalDate birth, String gender, RegionDto region) {
			this.birth = birth;
			this.gender = gender;
			this.region = region;
		}
	}

	@Getter
	public static class RegionDto {

		private final String province;
		private final String district;

		public RegionDto(String province, String district) {
			this.province = province;
			this.district = district;
		}
	}

	@Getter
	public static class Answer {

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
