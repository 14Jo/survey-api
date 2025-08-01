package com.example.surveyapi.domain.statistic.domain.model.response;

import java.time.LocalDateTime;
import java.util.stream.Stream;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TextResponse implements Response {

	private Long questionId;
	private AnswerType answerType;

	public static TextResponse of(Long questionId, AnswerType answerType) {
		TextResponse textResponse = new TextResponse();
		textResponse.questionId = questionId;
		textResponse.answerType = answerType;

		return textResponse;
	}

	@Override
	public Stream<Statistic.ChoiceIdentifier> getIdentifiers(LocalDateTime statisticHour) {
		return Stream.of(new Statistic.ChoiceIdentifier(
			questionId, null, answerType, statisticHour
		));
	}
}
