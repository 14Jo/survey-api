package com.example.surveyapi.domain.survey.application.qeury.dto;

import java.time.LocalDateTime;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

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
	private ScheduleState scheduleState;
	private SurveyOptions options;

	public static SurveySyncDto from(
		Long surveyId, Long projectId,
		String title, String description,
		SurveyStatus status, ScheduleState scheduleState,
		SurveyOption options, SurveyDuration duration
	) {
		SurveySyncDto dto = new SurveySyncDto();
		dto.surveyId = surveyId;
		dto.projectId = projectId;
		dto.title = title;
		dto.status = status;
		dto.scheduleState = scheduleState;
		dto.description = description;
		dto.options = new SurveyOptions(
			options.isAnonymous(), options.isAllowResponseUpdate(),
			duration.getStartDate(), duration.getEndDate()
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
