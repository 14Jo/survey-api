package com.example.surveyapi.domain.survey.application.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

import jakarta.validation.constraints.AssertTrue;
import lombok.Getter;

@Getter
public class UpdateSurveyRequest {

	private String title;

	private String description;

	private SurveyType surveyType;

	private SurveyDuration surveyDuration;

	private SurveyOption surveyOption;

	private List<QuestionInfo> questions;

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

	@AssertTrue(message = "설문 시작일은 종료일보다 이전이어야 합니다.")
	private boolean isStartBeforeEnd() {
		if (surveyDuration == null || surveyDuration.getStartDate() == null || surveyDuration.getEndDate() == null)
			return true;
		return surveyDuration.getStartDate().isBefore(surveyDuration.getEndDate());
	}

	@AssertTrue(message = "설문 종료일은 오늘 이후여야 합니다.")
	private boolean isEndAfterNow() {
		if (surveyDuration == null || surveyDuration.getEndDate() == null)
			return true;
		return surveyDuration.getEndDate().isAfter(LocalDateTime.now());
	}
}
