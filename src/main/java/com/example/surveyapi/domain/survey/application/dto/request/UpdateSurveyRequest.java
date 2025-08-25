package com.example.surveyapi.domain.survey.application.dto.request;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

@Getter
public class UpdateSurveyRequest extends SurveyRequest {

	@AssertTrue(message = "요청값이 단 한개도 입력되지 않았습니다.")
	private boolean isValidRequest() {
		return this.title != null || this.description != null || surveyType != null || surveyDuration != null
			|| this.questions != null || this.surveyOption != null;
	}

	@AssertTrue(message = "설문 기간이 들어온 경우, 시작일과 종료일이 모두 입력되어야 합니다.")
	private boolean isValidDurationPresence() {
		if (surveyDuration == null)
			return true;
		return surveyDuration.getStartDate() != null && surveyDuration.getEndDate() != null;
	}
}
