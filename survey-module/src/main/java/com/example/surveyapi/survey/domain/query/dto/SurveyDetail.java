package com.example.surveyapi.survey.domain.query.dto;

import java.util.List;

import com.example.surveyapi.survey.domain.survey.Survey;
import com.example.surveyapi.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.survey.domain.survey.vo.SurveyOption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SurveyDetail {
	private Long surveyId;
	private String title;
	private String description;
	private SurveyStatus status;
	private ScheduleState scheduleState;
	private SurveyDuration duration;
	private SurveyOption option;
	private List<QuestionInfo> questions;

	public static SurveyDetail of(Survey survey, List<QuestionInfo> questions) {
		SurveyDetail detail = new SurveyDetail();
		detail.surveyId = survey.getSurveyId();
		detail.title = survey.getTitle();
		detail.description = survey.getDescription();
		detail.status = survey.getStatus();
		detail.scheduleState = survey.getScheduleState();
		detail.duration = survey.getDuration();
		detail.option = survey.getOption();
		detail.questions = questions;
		return detail;
	}
}
