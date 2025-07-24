package com.example.surveyapi.domain.survey.domain.question;

import java.util.ArrayList;
import java.util.List;

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

		List<Choice> mutableChoices = new ArrayList<>(choices);

		mutableChoices.sort((c1, c2) -> Integer.compare(c1.getDisplayOrder(), c2.getDisplayOrder()));

		for (int i = 0; i < mutableChoices.size() - 1; i++) {
			Choice current = mutableChoices.get(i);
			Choice next = mutableChoices.get(i + 1);

			if (current.getDisplayOrder() == next.getDisplayOrder()) {

				for (int j = i + 1; j < mutableChoices.size(); j++) {
					Choice choiceToUpdate = mutableChoices.get(j);

					Choice updatedChoice = new Choice(choiceToUpdate.getContent(),
						choiceToUpdate.getDisplayOrder() + 1);
					mutableChoices.set(j, updatedChoice);
				}
			}
		}

		this.choices = mutableChoices;

	}
}
