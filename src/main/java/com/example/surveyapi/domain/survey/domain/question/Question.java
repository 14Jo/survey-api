package com.example.surveyapi.domain.survey.domain.question;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
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

	@Column(name = "survey_id", nullable = false)
	private Long surveyId;

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

	public static Question create(
		Long surveyId,
		String content,
		QuestionType type,
		int displayOrder,
		boolean isRequired,
		List<Choice> choices
	) {
		Question question = new Question();

		question.surveyId = surveyId;
		question.content = content;
		question.type = type;
		question.displayOrder = displayOrder;
		question.isRequired = isRequired;
		question.choices = choices;

		if (choices != null && !choices.isEmpty()) {
			question.duplicateChoiceOrder();
		}

		return question;
	}

	public void duplicateChoiceOrder() {
		if (choices == null || choices.isEmpty()) {
			return;
		}

		List<Choice> mutableChoices = new ArrayList<>();
		Set<Integer> usedOrders = new HashSet<>();

		for (Choice choice : choices) {
			int candidate = choice.getDisplayOrder();
			while (usedOrders.contains(candidate)) {
				candidate++;
			}
			mutableChoices.add(Choice.of(choice.getContent(), candidate));
			usedOrders.add(candidate);
		}

		this.choices = mutableChoices;
	}
}
