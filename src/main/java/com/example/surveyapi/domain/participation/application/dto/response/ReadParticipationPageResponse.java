package com.example.surveyapi.domain.participation.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.participation.application.dto.request.SurveyInfoOfParticipation;
import com.example.surveyapi.domain.participation.domain.participation.Participation;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReadParticipationPageResponse {

	private Long participationId;
	private SurveyInfoOfParticipation surveyInfo;
	private LocalDateTime participatedAt;

	public ReadParticipationPageResponse(Participation participation, SurveyInfoOfParticipation surveyInfo) {
		this.participationId = participation.getId();
		this.surveyInfo = surveyInfo;
		this.participatedAt = participation.getUpdatedAt();
	}

	public static ReadParticipationPageResponse of(Participation participation, SurveyInfoOfParticipation surveyInfo) {
		return new ReadParticipationPageResponse(participation, surveyInfo);
	}
}
