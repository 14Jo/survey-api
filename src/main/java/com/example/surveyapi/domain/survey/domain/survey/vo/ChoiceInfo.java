package com.example.surveyapi.domain.survey.domain.survey.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChoiceInfo {
	private String content;
	private Integer choiceId;

	public static ChoiceInfo of(String content, int displayOrder) {
		ChoiceInfo choiceInfo = new ChoiceInfo();
		choiceInfo.content = content;
		choiceInfo.choiceId = displayOrder;
		return choiceInfo;
	}
}