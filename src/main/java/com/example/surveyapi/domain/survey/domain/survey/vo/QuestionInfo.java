package com.example.surveyapi.domain.survey.domain.survey.vo;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;

import lombok.Getter;

@Getter
public class QuestionInfo {
	private final String content;
	private final QuestionType questionType;
	private final boolean isRequired;
	private final int displayOrder;
	private final List<ChoiceInfo> choices;

	public QuestionInfo(String content, QuestionType questionType, boolean isRequired, int displayOrder,
		List<ChoiceInfo> choices
	) {
		this.content = content;
		this.questionType = questionType;
		this.isRequired = isRequired;
		this.displayOrder = displayOrder;
		this.choices = choices;
	}
}