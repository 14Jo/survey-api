package com.example.surveyapi.domain.survey.domain.survey;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyCreatedEvent;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
public class Survey extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "survey_id")
	private Long surveyId;

	@Column(name = "projecy_id", nullable = false)
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

	@Transient
	private Optional<SurveyCreatedEvent> createdEvent;

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

		survey.projectId = projectId;
		survey.creatorId = creatorId;
		survey.title = title;
		survey.description = description;
		survey.type = type;
		survey.status = decideStatus(duration.getStartDate());
		survey.duration = duration;
		survey.option = option;

		survey.createdEvent = Optional.of(new SurveyCreatedEvent(questions));

		return survey;
	}

	private static SurveyStatus decideStatus(LocalDateTime startDate) {
		LocalDateTime now = LocalDateTime.now();
		if (startDate.isAfter(now)) {
			return SurveyStatus.PREPARING;
		} else {
			return SurveyStatus.IN_PROGRESS;
		}
	}

	public SurveyCreatedEvent getCreatedEvent() {
		SurveyCreatedEvent surveyCreatedEvent = this.createdEvent.orElseThrow(() -> {
			log.error("이벤트가 존재하지 않습니다.");
			return new CustomException(CustomErrorCode.SERVER_ERROR);
		});

		if(surveyCreatedEvent.getSurveyId().isEmpty()) {
			log.error("이벤트에 할당된 설문 ID가 없습니다.");
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "이벤트에 할당된 설문 ID가 없습니다.");
		}

		return surveyCreatedEvent;
	}

	public void saved() {
		this.createdEvent.ifPresent(surveyCreatedEvent ->
			surveyCreatedEvent.setSurveyId(this.getSurveyId()));
	}

	public void published() {
		this.createdEvent = Optional.empty();
	}

	public void open() {
		this.status = SurveyStatus.IN_PROGRESS;
	}

	public void close() {
		this.status = SurveyStatus.CLOSED;
	}
}
