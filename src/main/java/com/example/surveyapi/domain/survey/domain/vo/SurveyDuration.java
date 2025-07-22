package com.example.surveyapi.domain.survey.domain.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyDuration {

	private LocalDateTime startDate;
	private LocalDateTime endDate;
}
