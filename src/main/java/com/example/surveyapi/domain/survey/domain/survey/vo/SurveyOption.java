package com.example.surveyapi.domain.survey.domain.survey.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyOption {
	private boolean anonymous = false;
	private boolean allowResponseUpdate = false;

	public static SurveyOption of(boolean anonymous, boolean allowResponseUpdate) {
		SurveyOption option = new SurveyOption();
		option.anonymous = anonymous;
		option.allowResponseUpdate = allowResponseUpdate;
		return option;
	}
}
