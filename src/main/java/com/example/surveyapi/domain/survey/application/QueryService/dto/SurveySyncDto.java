package com.example.surveyapi.domain.survey.application.QueryService.dto;

import java.time.LocalDateTime;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveySyncDto {

	private Long surveyId;
	private Long projectId;
	private String title;
	private String description;
	private SurveyStatus status;
	private SurveyOptions options;

	public static SurveySyncDto from(Survey survey) {
		SurveySyncDto dto = new SurveySyncDto();
		dto.surveyId = survey.getSurveyId();
		dto.projectId = survey.getProjectId();
		dto.title = survey.getTitle();
		dto.status = survey.getStatus();
		dto.description = survey.getDescription();
		dto.options = new SurveyOptions(
			survey.getOption().isAnonymous(), survey.getOption().isAllowResponseUpdate(),
			survey.getDuration().getStartDate(), survey.getDuration().getEndDate()
		);

		return dto;
	}

	@Getter
	@AllArgsConstructor
	public static class SurveyOptions {
		private boolean anonymous;
		private boolean allowResponseUpdate;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
	}

}
