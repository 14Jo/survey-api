package com.example.surveyapi.survey.domain.survey.vo;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class SurveyDuration {

	@Column(name = "start_at", nullable = true)
	private LocalDateTime startDate;

	@Column(name = "end_at", nullable = true)
	private LocalDateTime endDate;

	public static SurveyDuration of(LocalDateTime startDate, LocalDateTime endDate) {
		SurveyDuration duration = new SurveyDuration();
		duration.startDate = startDate;
		duration.endDate = endDate;
		return duration;
	}
}
