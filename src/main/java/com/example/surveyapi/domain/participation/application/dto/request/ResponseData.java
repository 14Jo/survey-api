package com.example.surveyapi.domain.participation.application.dto.request;

import java.util.Map;

import com.example.surveyapi.domain.participation.domain.response.enums.QuestionType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ResponseData {

	@NotNull
	private Long questionId;
	@NotNull
	private QuestionType questionType;
	private Map<String, Object> answer;
}
