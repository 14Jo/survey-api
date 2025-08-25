package com.example.surveyapi.survey.domain.query;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.surveyapi.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.survey.domain.question.vo.Choice;
import com.example.surveyapi.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.survey.domain.survey.enums.SurveyStatus;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "survey_summaries")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class SurveyReadEntity {

	@Id
	private String id;

	@Indexed
	private Long surveyId;

	@Indexed
	private Long projectId;

	private String title;
	private String description;
	private String status;
	private String scheduleState;
	private Integer participationCount;

	private SurveyOptions options;
	@Setter
	private List<QuestionSummary> questions;

	public static SurveyReadEntity create(
		Long surveyId, Long projectId, String title,
		String description, SurveyStatus status, ScheduleState scheduleState,
		Integer participationCount, SurveyOptions options

	) {
		SurveyReadEntity surveyRead = new SurveyReadEntity();
		surveyRead.surveyId = surveyId;
		surveyRead.projectId = projectId;
		surveyRead.title = title;
		surveyRead.description = description;
		surveyRead.status = status.name();
		surveyRead.scheduleState = scheduleState.name();
		surveyRead.participationCount = participationCount;
		surveyRead.options = options;

		return surveyRead;
	}

	public void activate(SurveyStatus status) {
		this.status = status.name();
	}

	public void updateScheduleState(String scheduleState, String surveyStatus) {
		this.scheduleState = scheduleState;
		this.status = surveyStatus;
	}

	@Getter
	@AllArgsConstructor
	public static class SurveyOptions {
		private boolean anonymous;
		private boolean allowResponseUpdate;
		private LocalDateTime startDate;
		private LocalDateTime endDate;
	}

	@Getter
	@AllArgsConstructor
	public static class QuestionSummary {
		private Long questionId;
		private String content;
		private QuestionType questionType;
		private boolean isRequired;
		private int displayOrder;
		private List<Choice> choices;
	}

	public void updateParticipationCount(int participationCount) {
		this.participationCount = participationCount;
	}

	public void update(Long surveyId, Long projectId, String title, String description,
		SurveyStatus surveyStatus, ScheduleState scheduleState, boolean anonymous, boolean allowResponseUpdate,
		LocalDateTime startDate, LocalDateTime endDate) {

		this.surveyId = surveyId;
		this.projectId = projectId;
		this.title = title;
		this.description = description;
		this.status = surveyStatus.name();
		this.scheduleState = scheduleState.name();
		this.options = new SurveyOptions(anonymous, allowResponseUpdate, startDate, endDate);
	}
}
