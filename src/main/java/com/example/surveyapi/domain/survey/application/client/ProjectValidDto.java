package com.example.surveyapi.domain.survey.application.client;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectValidDto {

	private Boolean valid;

	public static ProjectValidDto of(List<Long> memberIds, Long userId) {
		ProjectValidDto dto = new ProjectValidDto();
		dto.valid = memberIds.contains(userId);
		return dto;
	}
}
