package com.example.surveyapi.domain.participation.application.client;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;

import lombok.Getter;

@Getter
public class SurveyInfoDto {
	
	private Long surveyId;
	private String title;
	private SurveyApiStatus status;
	private Option option;
	private Duration duration;

	@Getter
	public static class Duration {
		private LocalDateTime endDate;
	}

	@Getter
	public static class Option {
		private boolean allowResponseUpdate;
	}
}
