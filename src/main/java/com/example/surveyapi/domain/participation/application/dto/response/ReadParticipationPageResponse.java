package com.example.surveyapi.domain.participation.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.surveyapi.domain.participation.application.dto.request.SurveyInfoOfParticipation;
import com.example.surveyapi.domain.participation.domain.participation.Participation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadParticipationPageResponse {

	private Long participationId;
	private Long surveyId;
	private String surveyTitle;
	private String surveyStatus;
	private LocalDate endDate;
	private boolean allowResponseUpdate;
	private LocalDateTime participatedAt;

	public ReadParticipationPageResponse(Long participationId, Long surveyId, String surveyTitle, String surveyStatus,
		LocalDate endDate, Boolean allowResponseUpdate,
		LocalDateTime participatedAt) {
		this.participationId = participationId;
		this.surveyId = surveyId;
		this.surveyTitle = surveyTitle;
		this.surveyStatus = surveyStatus;
		this.endDate = endDate;
		this.allowResponseUpdate = allowResponseUpdate;
		this.participatedAt = participatedAt;
	}

	public static ReadParticipationPageResponse of(Participation participation, SurveyInfoOfParticipation surveyInfo) {
		return new ReadParticipationPageResponse(participation.getId(), participation.getSurveyId(),
			surveyInfo.getSurveyTitle(), surveyInfo.getSurveyStatus(), surveyInfo.getEndDate(),
			surveyInfo.isAllowResponseUpdate(),
			participation.getCreatedAt());
	}
}
