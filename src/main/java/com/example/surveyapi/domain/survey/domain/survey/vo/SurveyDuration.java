package com.example.surveyapi.domain.survey.domain.survey.vo;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyDuration {

	private LocalDateTime startDate;
	private LocalDateTime endDate;

	public static SurveyDuration of(LocalDateTime startDate, LocalDateTime endDate) {
		SurveyDuration duration = new SurveyDuration();
		duration.startDate = startDate;
		duration.endDate = endDate;
		return duration;
	}
}
