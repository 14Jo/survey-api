package com.example.surveyapi.domain.survey.domain.request;

import lombok.Getter;

@Getter
public class CreateChoiceRequest {
	private String content;
	private int displayOrder;
}
