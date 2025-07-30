package com.example.surveyapi.domain.survey.domain.survey.vo;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
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

	@AssertTrue(message = "다중 선택지 문항에 선택지가 없습니다.")
	public boolean isValid() {
		if (questionType == QuestionType.MULTIPLE_CHOICE) {
			return choices != null && choices.size() > 1;
		}
		return true;
	}
}