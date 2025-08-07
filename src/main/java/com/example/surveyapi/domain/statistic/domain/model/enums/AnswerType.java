package com.example.surveyapi.domain.statistic.domain.model.enums;

import java.util.Arrays;

import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AnswerType {
	SINGLE_CHOICE("choice"),
	MULTIPLE_CHOICE("choices"),
	TEXT_ANSWER("textAnswer");

	private final String key;

	public static AnswerType findByKey(String key) {
		return Arrays.stream(values())
			.filter(type -> type.key.equals(key))
			.findFirst()
			.orElseThrow(() -> new CustomException(CustomErrorCode.ANSWER_TYPE_NOT_FOUND));
	}
}