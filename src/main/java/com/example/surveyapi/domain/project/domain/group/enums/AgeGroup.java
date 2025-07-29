package com.example.surveyapi.domain.project.domain.group.enums;

import lombok.Getter;

@Getter
public enum AgeGroup {
	TWENTIES("20대 그룹"), // 20대
	THIRTIES("30대 그룹"), // 30대
	FORTIES("40대 그룹"), // 40대
	OTHERS("그 외 연령"); // 그 외 연령

	private String groupName;

	AgeGroup(String groupName) {
		this.groupName = groupName;
	}
}
