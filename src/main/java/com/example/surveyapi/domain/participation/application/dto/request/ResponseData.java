package com.example.surveyapi.domain.participation.application.dto.request;

import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ResponseData {

	@NotNull
	private Long questionId;
	private Map<String, Object> answer;
}
