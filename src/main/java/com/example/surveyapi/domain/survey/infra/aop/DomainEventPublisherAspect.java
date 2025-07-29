package com.example.surveyapi.domain.survey.infra.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.event.AbstractRoot;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DomainEventPublisherAspect {

	private final ApplicationEventPublisher eventPublisher;

	@AfterReturning(pointcut = "com.example.surveyapi.domain.survey.infra.aop.SurveyPointcuts.surveyPointCut(entity)", argNames = "entity")
	public void afterSave(Object entity) {
		if (entity instanceof AbstractRoot aggregateRoot) {
			registerEvent(aggregateRoot);
			aggregateRoot.pollAllEvents()
				.forEach(eventPublisher::publishEvent);
		}
	}

	private void registerEvent(AbstractRoot root) {
		if (root instanceof Survey survey) {
			root.setCreateEventId(survey.getSurveyId());
		}
	}
}
