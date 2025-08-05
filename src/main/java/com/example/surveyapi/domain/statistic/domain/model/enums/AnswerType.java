package com.example.surveyapi.domain.statistic.domain.model.enums;

import java.util.Arrays;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AnswerType {
	SINGLE_CHOICE("choice"),
	MULTIPLE_CHOICE("choices"),
	TEXT_ANSWER("textAnswer");

	private final String key;

	public static Optional<AnswerType> findByKey(String key) {
		return Arrays.stream(values())
			.filter(type -> type.key.equals(key))
			.findFirst();
	}
}