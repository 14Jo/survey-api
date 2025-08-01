package com.example.surveyapi.domain.survey.application.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchSurveyTitleResponse {
	private Long surveyId;
	private String title;
	private SurveyStatus status;
	private Option option;
	private Duration duration;
	private Integer participationCount;

	public static SearchSurveyTitleResponse from(SurveyTitle surveyTitle, Integer count) {
		SearchSurveyTitleResponse response = new SearchSurveyTitleResponse();
		response.surveyId = surveyTitle.getSurveyId();
		response.title = surveyTitle.getTitle();
		response.status = surveyTitle.getStatus();
		response.option = Option.from(surveyTitle.getOption());
		response.duration = Duration.from(surveyTitle.getDuration());
		response.participationCount = count;
		return response;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Duration {
		private LocalDateTime startDate;
		private LocalDateTime endDate;

		public static Duration from(SurveyDuration duration) {
			Duration result = new Duration();
			result.startDate = duration.getStartDate();
			result.endDate = duration.getEndDate();
			return result;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Option {
		private boolean anonymous = false;
		private boolean allowResponseUpdate = false;

		public static Option from(SurveyOption option) {
			Option result = new Option();
			result.anonymous = option.isAnonymous();
			result.allowResponseUpdate = option.isAllowResponseUpdate();
			return result;
		}
	}
}
