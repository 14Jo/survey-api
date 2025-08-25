package com.example.surveyapi.domain.participation.application.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.surveyapi.domain.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;
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
		private String title;
		private SurveyApiStatus status;
		private LocalDate endDate;
		private boolean allowResponseUpdate;

		public static SurveyInfoOfParticipation from(SurveyInfoDto surveyInfoDto) {
			SurveyInfoOfParticipation surveyInfo = new SurveyInfoOfParticipation();
			surveyInfo.surveyId = surveyInfoDto.getSurveyId();
			surveyInfo.title = surveyInfoDto.getTitle();
			surveyInfo.status = surveyInfoDto.getStatus();
			surveyInfo.endDate = surveyInfoDto.getDuration().getEndDate().toLocalDate();
			surveyInfo.allowResponseUpdate = surveyInfoDto.getOption().isAllowResponseUpdate();

			return surveyInfo;
		}
	}
}
