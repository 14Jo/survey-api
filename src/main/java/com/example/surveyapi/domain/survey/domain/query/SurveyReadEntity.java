package com.example.surveyapi.domain.survey.domain.query;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;

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
	private Integer participationCount;

	private SurveyOptions options;
	@Setter
	private List<QuestionSummary> questions;

	public static SurveyReadEntity create(
		Long surveyId, Long projectId, String title,
		String description, String status, Integer participationCount,
		SurveyOptions options

	) {
		SurveyReadEntity surveyRead = new SurveyReadEntity();
		surveyRead.surveyId = surveyId;
		surveyRead.projectId = projectId;
		surveyRead.title = title;
		surveyRead.description = description;
		surveyRead.status = status;
		surveyRead.participationCount = participationCount;
		surveyRead.options = options;

		return surveyRead;
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
}






