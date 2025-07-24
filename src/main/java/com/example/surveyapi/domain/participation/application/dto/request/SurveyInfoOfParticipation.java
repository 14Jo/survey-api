package com.example.surveyapi.domain.participation.application.dto.request;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SurveyInfoOfParticipation {

	private Long surveyId;
	private String surveyTitle;
	private String surveyStatus;
	private LocalDate endDate;
	private boolean allowResponseUpdate;
}
