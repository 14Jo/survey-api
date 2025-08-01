package com.example.surveyapi.domain.statistic.domain.model.response;

import java.util.List;
import java.util.Map;

import com.example.surveyapi.domain.statistic.domain.dto.StatisticCommand;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ResponseFactory {

	public static Response createFrom(StatisticCommand.ResponseData data) {
		Long questionId = data.questionId();
		Map<String, Object> answer = data.answer();

		if (answer.containsKey(AnswerType.SINGLE_CHOICE.getKey())) {
			List<?> rawList = (List<?>) answer.get(AnswerType.SINGLE_CHOICE.getKey());
			List<Long> choices = rawList.stream()
				.map(num -> ((Number) num).longValue())
				.toList();

			return ChoiceResponse.of(questionId, choices, AnswerType.SINGLE_CHOICE);
		}

		if (answer.containsKey(AnswerType.MULTIPLE_CHOICE.getKey())) {
			List<?> rawList = (List<?>) answer.get(AnswerType.MULTIPLE_CHOICE.getKey());
			List<Long> choices = rawList.stream()
				.map(num -> ((Number) num).longValue())
				.toList();

			return ChoiceResponse.of(questionId, choices, AnswerType.MULTIPLE_CHOICE);
		}

		if (answer.containsKey(AnswerType.TEXT_ANSWER.getKey())) {
			return TextResponse.of(questionId, AnswerType.TEXT_ANSWER);
		}

		log.error("Answer Type is not supported or empty answer type: {}", answer);
		throw new CustomException(CustomErrorCode.ANSWER_TYPE_NOT_FOUND);
	}
}
