package com.example.surveyapi.domain.survey.domain.question.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Choice {
	private String content;
	private int displayOrder;
}
