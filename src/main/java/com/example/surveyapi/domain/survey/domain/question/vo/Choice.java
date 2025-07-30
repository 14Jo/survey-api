package com.example.surveyapi.domain.survey.domain.question.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Choice {
	private String content;
	private int displayOrder;

	public static Choice of(String content, int displayOrder) {
		Choice choice = new Choice();
		choice.content = content;
		choice.displayOrder = displayOrder;
		return choice;
	}


}
