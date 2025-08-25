package com.example.surveyapi.participation.application.dto.response;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationGroupResponse {

	private Long surveyId;
	private List<ParticipationDetailResponse> participations;

	public static ParticipationGroupResponse of(Long surveyId, List<ParticipationDetailResponse> participations) {
		ParticipationGroupResponse participationGroup = new ParticipationGroupResponse();
		participationGroup.surveyId = surveyId;
		participationGroup.participations = participations;

		return participationGroup;
	}
}
