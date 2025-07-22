package com.example.surveyapi.domain.survey.domain;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.enums.SurveyType;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Survey extends BaseEntity {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_id")
	private Long surveyId;

	@Column(name = "projecy_id",  nullable = false)
	private Long projectId;
	@Column(name = "creator_id",  nullable = false)
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
	@Column(name = "is_anonymous", nullable = false)
	private Boolean isAnonymous = false;
	@Column(name = "allow_multiple_responses", nullable = false)
	private Boolean allowMultipleResponses = false;
	@Column(name = "allow_response_update", nullable = false)
	private Boolean allowResponseUpdate = false;
	@Column(name = "start_date", nullable = false)
	private LocalDateTime startDate;
	@Column(name = "end_date", nullable = false)
	private LocalDateTime endDate;


	public static Survey create(
		Long projectId,
		Long creatorId,
		String title,
		String description,
		SurveyType type,
		SurveyStatus status,
		Boolean isAnonymous,
		Boolean allowMultipleResponses,
		Boolean allowResponseUpdate,
		LocalDateTime startDate,
		LocalDateTime endDate
	) {
		Survey survey = new Survey();
		survey.projectId = projectId;
		survey.creatorId = creatorId;
		survey.title = title;
		survey.description = description;
		survey.type = type;
		survey.status = status;
		survey.isAnonymous = isAnonymous;
		survey.allowMultipleResponses = allowMultipleResponses;
		survey.allowResponseUpdate = allowResponseUpdate;
		survey.startDate = startDate;
		survey.endDate = endDate;
		return survey;
	}
}
