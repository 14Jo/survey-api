package com.example.surveyapi.domain.participation.application.client;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.example.surveyapi.domain.participation.application.client.enums.SurveyApiStatus;

import lombok.Getter;

@Getter
public class SurveyInfoDto implements Serializable {

	private Long surveyId;
	private String title;
	private SurveyApiStatus status;
	private Option option;
	private Duration duration;

	@Getter
	public static class Duration implements Serializable {
		private LocalDateTime endDate;
	}

	@Getter
	public static class Option implements Serializable {
		private boolean allowResponseUpdate;
	}
}
