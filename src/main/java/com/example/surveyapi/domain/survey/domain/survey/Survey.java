package com.example.surveyapi.domain.survey.domain.survey;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.survey.domain.survey.event.ActivateEvent;
import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.event.AbstractRoot;
import com.example.surveyapi.domain.survey.domain.survey.event.SurveyScheduleRequestedEvent;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
public class Survey extends AbstractRoot<Survey> {

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

	@OneToMany(
		mappedBy = "survey",
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE,
			CascadeType.REFRESH
		},
		fetch = FetchType.LAZY
	)
	@OrderBy("displayOrder ASC")
	private List<Question> questions = new ArrayList<>();

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
		survey.status = SurveyStatus.PREPARING;
		survey.duration = duration;
		survey.option = option;
		survey.addQuestion(questions);

		survey.registerScheduledEvent();

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
					this.addQuestion(questions);
				}
			}
		});
		this.registerScheduledEvent();
	}

	public void open() {
		this.status = SurveyStatus.IN_PROGRESS;
		this.duration = SurveyDuration.of(LocalDateTime.now(), this.duration.getEndDate());
		registerEvent(new ActivateEvent(this.surveyId, this.creatorId, this.status, this.duration.getEndDate()));
	}

	public void close() {
		this.status = SurveyStatus.CLOSED;
		this.duration = SurveyDuration.of(this.duration.getStartDate(), LocalDateTime.now());
		registerEvent(new ActivateEvent(this.surveyId, this.creatorId, this.status, this.duration.getEndDate()));
	}

	public void delete() {
		this.status = SurveyStatus.DELETED;
		this.duration = SurveyDuration.of(this.duration.getStartDate(), LocalDateTime.now());
		this.isDeleted = true;
		removeQuestions();
	}

	private void addQuestion(List<QuestionInfo> questions) {
		try {
			List<Question> questionList = questions.stream().map(questionInfo -> {
				return Question.create(
					this,
					questionInfo.getContent(), questionInfo.getQuestionType(),
					questionInfo.getDisplayOrder(), questionInfo.isRequired(),
					questionInfo.getChoices());
			}).toList();
			this.questions.addAll(questionList);
		} catch (NullPointerException e) {
			log.error("질문 null {}", e.getMessage());
			throw new CustomException(CustomErrorCode.SERVER_ERROR, e.getMessage());
		}
	}

	private void removeQuestions() {
		this.questions.forEach(Question::delete);
	}

	private void registerScheduledEvent() {
		this.registerEvent(new SurveyScheduleRequestedEvent(
			this.getSurveyId(),
			this.getCreatorId(),
			this.getDuration().getStartDate(),
			this.getDuration().getEndDate()
		));
	}
}
