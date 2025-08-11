package com.example.surveyapi.domain.survey.application.qeury.dto;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionSyncDto {
	Long questionId;
	String content;
	QuestionType type;
	int displayOrder;
	boolean isRequired;
	List<ChoiceDto> choices;

	public static QuestionSyncDto from(Question question) {
		QuestionSyncDto dto = new QuestionSyncDto();
		dto.questionId = question.getQuestionId();
		dto.content = question.getContent();
		dto.type = question.getType();
		dto.displayOrder = question.getDisplayOrder();
		dto.isRequired = question.isRequired();
		dto.choices = question.getChoices().stream().map(ChoiceDto::of).toList();

		return dto;
	}

	@Getter
	public static class ChoiceDto {
		private String content;
		private int displayOrder;

		public static ChoiceDto of(Choice choice) {
			ChoiceDto dto = new ChoiceDto();
			dto.content = choice.getContent();
			dto.displayOrder = choice.getDisplayOrder();
			return dto;
		}
	}
}
