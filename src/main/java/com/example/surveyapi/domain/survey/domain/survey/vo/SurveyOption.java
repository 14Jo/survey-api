package com.example.surveyapi.domain.survey.domain.survey.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Embeddable
public class SurveyOption {
	@Column(name = "anonymous", nullable = false)
	private boolean anonymous;

	@Column(name = "allow_response_update", nullable = false)
	private boolean allowResponseUpdate;

	public static SurveyOption of(boolean anonymous, boolean allowResponseUpdate) {
		SurveyOption option = new SurveyOption();
		option.anonymous = anonymous;
		option.allowResponseUpdate = allowResponseUpdate;
		return option;
	}
}
