package com.example.surveyapi.participation.domain.command;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ResponseData {

	@NotNull
	private Long questionId;
	private Map<String, Object> answer;
}
