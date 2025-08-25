package com.example.surveyapi.domain.participation.domain.participation.query;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class ParticipationInfo {

	private Long participationId;
	private Long surveyId;
	private LocalDateTime participatedAt;

	public ParticipationInfo(Long participationId, Long surveyId, LocalDateTime participatedAt) {
		this.participationId = participationId;
		this.surveyId = surveyId;
		this.participatedAt = participatedAt;
	}
}
