package com.example.surveyapi.domain.survey.infra.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DomainEventPublisherAspect {

	private final ApplicationEventPublisher eventPublisher;

	@AfterReturning(pointcut = "com.example.surveyapi.domain.survey.infra.aop.SurveyPointcuts.surveyCreatePointcut(survey)", argNames = "survey")
	public void publishCreateEvent(Survey survey) {
		if (survey != null) {
			survey.registerCreatedEvent();
			eventPublisher.publishEvent(survey.getCreatedEvent());
			survey.clearCreatedEvent();
		}
	}

	@AfterReturning(pointcut = "com.example.surveyapi.domain.survey.infra.aop.SurveyPointcuts.surveyDeletePointcut(survey)", argNames = "survey")
	public void publishDeleteEvent(Survey survey) {
		if (survey != null) {
			survey.registerDeletedEvent();
			eventPublisher.publishEvent(survey.getDeletedEvent());
			survey.clearDeletedEvent();
		}
	}
}
