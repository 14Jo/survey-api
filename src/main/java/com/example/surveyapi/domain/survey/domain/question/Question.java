package com.example.surveyapi.domain.survey.domain.question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Getter
@NoArgsConstructor
public class Question extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	private Long questionId;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(name = "type", nullable = false)
	private QuestionType type = QuestionType.SINGLE_CHOICE;

	@Setter
	@Column(name = "display_order", nullable = false)
	private Integer displayOrder;

	@Column(name = "is_required", nullable = false)
	private boolean isRequired = false;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "choices", columnDefinition = "jsonb")
	private List<Choice> choices = new ArrayList<>();

	@ManyToOne(
		fetch = FetchType.LAZY,
		optional = false
	)
	@JoinColumn(
		name = "survey_id",
		nullable = false
	)
	private Survey survey;

	public static Question create(
		Survey survey,
		String content,
		QuestionType type,
		int displayOrder,
		boolean isRequired,
		List<ChoiceInfo> choices
	) {
		Question question = new Question();
		question.survey = survey;
		question.content = content;
		question.type = type;
		question.displayOrder = displayOrder;
		question.isRequired = isRequired;
		question.addChoice(choices);

		return question;
	}

	private void addChoice(List<ChoiceInfo> choices) {
		try {
			List<Choice> choiceList = choices.stream().map(choiceInfo -> {
				return Choice.of(choiceInfo.getContent(), choiceInfo.getDisplayOrder());
			}).toList();
			this.choices.addAll(choiceList);
		} catch (NullPointerException e) {
			log.error("선택지 null {}", e.getMessage());
			throw new CustomException(CustomErrorCode.SERVER_ERROR, e.getMessage());
		}
	}
}
