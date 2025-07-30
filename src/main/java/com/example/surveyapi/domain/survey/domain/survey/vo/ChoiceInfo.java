package com.example.surveyapi.domain.survey.domain.survey.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class ChoiceInfo {
	private final String content;
	private final int displayOrder;

	public ChoiceInfo(String content, int displayOrder) {
		this.content = content;
		this.displayOrder = displayOrder;
	}
}