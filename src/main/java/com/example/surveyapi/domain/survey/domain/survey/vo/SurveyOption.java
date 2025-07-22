package com.example.surveyapi.domain.survey.domain.survey.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SurveyOption {
	private boolean anonymous = false;
	private boolean allowMultipleResponses = false;
	private boolean allowResponseUpdate = false;

}
