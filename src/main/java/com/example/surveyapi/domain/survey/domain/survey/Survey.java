package com.example.surveyapi.domain.survey.domain.survey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.event.AbstractRoot;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyCreatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyDeletedEvent;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyUpdatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.event.SurveyActivateEvent;
import com.example.surveyapi.global.exception.CustomException;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
public class Survey extends AbstractRoot {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_id")
	private Long surveyId;

	@Column(name = "project_id", nullable = false)
	private Long projectId;
	@Column(name = "creator_id", nullable = false)
	private Long creatorId;
	@Column(name = "title", nullable = false)
	private String title;
	@Column(name = "description", columnDefinition = "TEXT")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private SurveyType type;
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private SurveyStatus status;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "survey_option", nullable = false, columnDefinition = "jsonb")
	private SurveyOption option;
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "survey_duration", nullable = false, columnDefinition = "jsonb")
	private SurveyDuration duration;

	public static Survey create(
		Long projectId,
		Long creatorId,
		String title,
		String description,
		SurveyType type,
		SurveyDuration duration,
		SurveyOption option,
		List<QuestionInfo> questions
	) {
		Survey survey = new Survey();

		try {
			survey.projectId = projectId;
			survey.creatorId = creatorId;
			survey.title = title;
			survey.description = description;
			survey.type = type;
			survey.status = SurveyStatus.PREPARING;
			survey.duration = duration;
			survey.option = option;

			survey.registerEvent(new SurveyCreatedEvent(questions), EventCode.SURVEY_CREATED);
		} catch (NullPointerException ex) {
			log.error(ex.getMessage(), ex);
			throw new CustomException(CustomErrorCode.SERVER_ERROR);
		}

		return survey;
	}

	public void updateFields(Map<String, Object> fields) {
		fields.forEach((key, value) -> {
			switch (key) {
				case "title" -> this.title = (String)value;
				case "description" -> this.description = (String)value;
				case "type" -> this.type = (SurveyType)value;
				case "duration" -> this.duration = (SurveyDuration)value;
				case "option" -> this.option = (SurveyOption)value;
				case "questions" -> {
					List<QuestionInfo> questions = (List<QuestionInfo>)value;
					registerEvent(new SurveyUpdatedEvent(this.surveyId, questions), EventCode.SURVEY_UPDATED);
				}
			}
		});
	}

	public void open() {
		this.status = SurveyStatus.IN_PROGRESS;
		this.duration = SurveyDuration.of(LocalDateTime.now(), this.duration.getEndDate());
		registerEvent(new SurveyActivateEvent(this.surveyId, this.creatorId, this.status, this.duration.getEndDate()), EventCode.SURVEY_ACTIVATED);
	}

	public void close() {
		this.status = SurveyStatus.CLOSED;
		this.duration = SurveyDuration.of(this.duration.getStartDate(), LocalDateTime.now());
		registerEvent(new SurveyActivateEvent(this.surveyId, this.creatorId, this.status, this.duration.getEndDate()), EventCode.SURVEY_ACTIVATED);
	}

	public void delete() {
		this.status = SurveyStatus.DELETED;
		this.isDeleted = true;
		registerEvent(new SurveyDeletedEvent(this.surveyId), EventCode.SURVEY_DELETED);
	}
}
