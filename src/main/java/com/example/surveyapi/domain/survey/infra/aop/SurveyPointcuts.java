package com.example.surveyapi.domain.survey.infra.aop;

import org.aspectj.lang.annotation.Pointcut;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

public class SurveyPointcuts {

	@Pointcut("@annotation(com.example.surveyapi.domain.survey.infra.annotation.SurveyEvent) && args(entity)")
	public void surveyPointCut(Object entity) {
	}
}
