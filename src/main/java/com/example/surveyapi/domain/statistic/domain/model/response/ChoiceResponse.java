package com.example.surveyapi.domain.statistic.domain.model.response;

import java.util.List;
import java.util.stream.Stream;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChoiceResponse implements Response {

	private Long questionId;
	private List<Long> choiceIds;
	private AnswerType answerType;

	public static ChoiceResponse of(Long questionId, List<Long> choiceIds, AnswerType type) {
		ChoiceResponse choiceResponse = new ChoiceResponse();
		choiceResponse.questionId = questionId;
		choiceResponse.choiceIds = choiceIds;
		choiceResponse.answerType = type;

		return choiceResponse;
	}

	@Override
	public Stream<Statistic.ChoiceIdentifier> getIdentifiers() {
		return this.choiceIds.stream()
			.map(choiceId -> new Statistic.ChoiceIdentifier(
				questionId, choiceId, answerType
			));
	}
}
