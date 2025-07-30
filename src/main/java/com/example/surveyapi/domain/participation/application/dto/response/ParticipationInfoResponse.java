package com.example.surveyapi.domain.participation.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationInfoResponse {

	private Long participationId;
	private SurveyInfoOfParticipation surveyInfo;
	private LocalDateTime participatedAt;

	public static ParticipationInfoResponse of(ParticipationInfo participationInfo,
		SurveyInfoOfParticipation surveyInfo) {
		ParticipationInfoResponse participationInfoResponse = new ParticipationInfoResponse();
		participationInfoResponse.participationId = participationInfo.getParticipationId();
		participationInfoResponse.participatedAt = participationInfo.getParticipatedAt();
		participationInfoResponse.surveyInfo = surveyInfo;

		return participationInfoResponse;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class SurveyInfoOfParticipation {

		private Long surveyId;
		private String surveyTitle;
		private String surveyStatus;
		private LocalDate endDate;
		private boolean allowResponseUpdate;

		// TODO: 타 도메인 통신으로 받는 데이터
		public static SurveyInfoOfParticipation of(Long surveyId, String surveyTitle, String surveyStatus,
			LocalDate endDate, boolean allowResponseUpdate) {
			SurveyInfoOfParticipation surveyInfo = new SurveyInfoOfParticipation();
			surveyInfo.surveyId = surveyId;
			surveyInfo.surveyTitle = surveyTitle;
			surveyInfo.surveyStatus = surveyStatus;
			surveyInfo.endDate = endDate;
			surveyInfo.allowResponseUpdate = allowResponseUpdate;

			return surveyInfo;
		}
	}
}
