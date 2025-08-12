package com.example.surveyapi.domain.survey.domain.question.vo;

import java.util.UUID;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Choice {
	private UUID choiceId;
	private String content;
	private int displayOrder;

	public static Choice of(String content, int displayOrder) {
		Choice choice = new Choice();
		choice.choiceId = UUID.randomUUID();
		choice.content = content;
		choice.displayOrder = displayOrder;
		return choice;
	}

	public static Choice of(UUID choiceId, String content, int displayOrder) {
		Choice choice = new Choice();
		choice.choiceId = choiceId;
		choice.content = content;
		choice.displayOrder = displayOrder;
		return choice;
	}
}
