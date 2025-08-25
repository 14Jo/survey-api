package com.example.surveyapi.survey.domain.query.dto;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyStatusList {
	private List<Long> surveyIds;

	public SurveyStatusList(List<Long> surveyIds) {
		this.surveyIds = surveyIds;
	}
}
