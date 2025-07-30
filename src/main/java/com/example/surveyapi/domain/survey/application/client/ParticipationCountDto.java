package com.example.surveyapi.domain.survey.application.client;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationCountDto {

	private Map<String, Integer> surveyPartCounts;
	
	public static ParticipationCountDto of(Map<String, Integer> surveyCounts) {
		ParticipationCountDto dto = new ParticipationCountDto();
		dto.surveyPartCounts = surveyCounts;
		return dto;
	}
}
