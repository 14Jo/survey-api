package com.example.surveyapi.domain.survey.domain.query.dto;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyDetail {
	private String title;
	private String description;
	private SurveyDuration duration;
	private SurveyOption option;
	private List<QuestionInfo> questions;

	public static SurveyDetail of(String title, String description, SurveyDuration duration, SurveyOption option, List<QuestionInfo> questions) {
		SurveyDetail detail = new SurveyDetail();
		detail.title = title;
		detail.description = description;
		detail.duration = duration;
		detail.option = option;
		detail.questions = questions;
		return detail;
	}
}
