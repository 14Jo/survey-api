package com.example.surveyapi.domain.participation.application.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchParticipationResponse {

	private Long surveyId;
	private List<ReadParticipationResponse> participations;

	public SearchParticipationResponse(Long surveyId, List<ReadParticipationResponse> participations) {
		this.surveyId = surveyId;
		this.participations = participations;
	}
}
