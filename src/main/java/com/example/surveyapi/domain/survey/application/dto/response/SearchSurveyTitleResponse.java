package com.example.surveyapi.domain.survey.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchSurveyTitleResponse {
	private Long surveyId;
	private String title;
	private String status;
	private Option option;
	private Duration duration;
	private Integer participationCount;

	public static SearchSurveyTitleResponse from(SurveyTitle surveyTitle, Integer count) {
		SearchSurveyTitleResponse response = new SearchSurveyTitleResponse();
		response.surveyId = surveyTitle.getSurveyId();
		response.title = surveyTitle.getTitle();
		response.status = surveyTitle.getStatus().name();
		response.option = Option.from(surveyTitle.getOption().isAnonymous(), surveyTitle.getOption().isAnonymous());
		response.duration = Duration.from(surveyTitle.getDuration().getStartDate(),
			surveyTitle.getDuration().getEndDate());
		response.participationCount = count;
		return response;
	}

	public static SearchSurveyTitleResponse from(SurveyReadEntity entity) {
		SearchSurveyTitleResponse response = new SearchSurveyTitleResponse();
		response.surveyId = entity.getSurveyId();
		response.title = entity.getTitle();
		response.status = entity.getStatus();

		if (entity.getOptions() != null) {
			response.option = Option.from(entity.getOptions().isAnonymous(),
				entity.getOptions().isAllowResponseUpdate());
			response.duration = Duration.from(entity.getOptions().getStartDate(), entity.getOptions().getEndDate());
		}

		response.participationCount = entity.getParticipationCount();
		return response;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Duration {
		private LocalDateTime startDate;
		private LocalDateTime endDate;

		public static Duration from(LocalDateTime startDate, LocalDateTime endDate) {
			Duration result = new Duration();
			result.startDate = startDate;
			result.endDate = endDate;
			return result;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Option {
		private boolean anonymous = false;
		private boolean allowResponseUpdate = false;

		public static Option from(boolean anonymous, boolean allowResponseUpdate) {
			Option result = new Option();
			result.anonymous = anonymous;
			result.allowResponseUpdate = allowResponseUpdate;
			return result;
		}
	}
}
