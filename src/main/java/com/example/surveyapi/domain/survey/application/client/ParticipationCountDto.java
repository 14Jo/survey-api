package com.example.surveyapi.domain.survey.application.client;

import java.util.Map;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ParticipationCountDto {

	private Map<Long, Long> surveyCounts;
	
	public static ParticipationCountDto of(Map<Long, Long> surveyCounts) {
		ParticipationCountDto dto = new ParticipationCountDto();
		dto.surveyCounts = surveyCounts;
		return dto;
	}
}
