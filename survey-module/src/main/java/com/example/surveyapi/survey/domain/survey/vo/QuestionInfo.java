package com.example.surveyapi.survey.domain.survey.vo;

import java.util.List;

import com.example.surveyapi.survey.domain.question.enums.QuestionType;

import jakarta.validation.constraints.AssertTrue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionInfo {
	private Long questionId;
	private String content;
	private QuestionType questionType;
	private boolean isRequired;
	private int displayOrder;
	private List<ChoiceInfo> choices;

	public static QuestionInfo of(Long questionId, String content, QuestionType questionType, boolean isRequired,
		int displayOrder, List<ChoiceInfo> choices) {
		QuestionInfo questionInfo = new QuestionInfo();
		questionInfo.questionId = questionId;
		questionInfo.content = content;
		questionInfo.questionType = questionType;
		questionInfo.isRequired = isRequired;
		questionInfo.displayOrder = displayOrder;
		questionInfo.choices = choices;
		return questionInfo;
	}

	public static QuestionInfo of(String content, QuestionType questionType, boolean isRequired,
		int displayOrder, List<ChoiceInfo> choices) {
		QuestionInfo questionInfo = new QuestionInfo();
		questionInfo.content = content;
		questionInfo.questionType = questionType;
		questionInfo.isRequired = isRequired;
		questionInfo.displayOrder = displayOrder;
		questionInfo.choices = choices;
		return questionInfo;
	}

	@AssertTrue(message = "다중 선택지 문항에 선택지가 없습니다.")
	public boolean isValid() {
		if (questionType == QuestionType.MULTIPLE_CHOICE) {
			return choices != null && choices.size() > 1;
		}
		return true;
	}
}