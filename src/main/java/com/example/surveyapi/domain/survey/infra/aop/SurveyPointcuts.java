package com.example.surveyapi.domain.survey.infra.aop;

import org.aspectj.lang.annotation.Pointcut;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

public class SurveyPointcuts {

	@Pointcut("@annotation(com.example.surveyapi.domain.survey.infra.annotation.SurveyCreate) && args(survey)")
	public void surveyCreatePointcut(Survey survey) {
	}

	@Pointcut("@annotation(com.example.surveyapi.domain.survey.infra.annotation.SurveyDelete) && args(survey)")
	public void surveyDeletePointcut(Survey survey) {
	}

	@Pointcut("@annotation(com.example.surveyapi.domain.survey.infra.annotation.SurveyUpdate) && args(survey)")
	public void surveyUpdatePointcut(Survey survey) {
	}
}
