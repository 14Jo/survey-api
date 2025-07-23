package com.example.surveyapi.domain.survey.infra;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DomainEventPublisherAspect {

	private final ApplicationEventPublisher eventPublisher;

	@Pointcut("execution(* org.springframework.data.repository.Repository+.save(..)) && args(survey)")
	public void surveySave(Survey survey) {
	}

	@AfterReturning(pointcut = "surveySave(survey)", argNames = "survey")
	public void publishEvents(Survey survey) {
		if (survey != null) {
			survey.saved();
			eventPublisher.publishEvent(survey.getCreatedEvent());
			survey.published();
		}
	}
}
