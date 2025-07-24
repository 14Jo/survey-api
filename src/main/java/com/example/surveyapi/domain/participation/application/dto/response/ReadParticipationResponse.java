package com.example.surveyapi.domain.participation.application.dto.response;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReadParticipationResponse {

	private Long participationId;
	private List<AnswerDetail> responses;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AnswerDetail {
		private Long questionId;
		private Map<String, Object> answer;
	}
}
