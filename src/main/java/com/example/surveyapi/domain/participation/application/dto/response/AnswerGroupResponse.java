package com.example.surveyapi.domain.participation.application.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerGroupResponse {

	private Long questionId;
	private List<Map<String, Object>> answers;

	public static AnswerGroupResponse of(Long questionId, List<Map<String, Object>> answer) {
		AnswerGroupResponse answerGroupResponse = new AnswerGroupResponse();
		answerGroupResponse.questionId = questionId;
		answerGroupResponse.answers = answer;

		return answerGroupResponse;
	}
}
