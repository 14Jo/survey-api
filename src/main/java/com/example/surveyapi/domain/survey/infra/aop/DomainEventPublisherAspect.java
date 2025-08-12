package com.example.surveyapi.domain.survey.infra.aop;

import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.event.AbstractRoot;
import com.example.surveyapi.domain.survey.infra.event.EventPublisher;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.SurveyEvent;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class DomainEventPublisherAspect {

	private final EventPublisher eventPublisher;

	@Async
	@AfterReturning(pointcut = "com.example.surveyapi.domain.survey.infra.aop.SurveyPointcuts.surveyPointCut(entity)", argNames = "entity")
	public void afterSave(Object entity) {
		if (entity instanceof AbstractRoot aggregateRoot) {
			registerEvent(aggregateRoot);

			Map<EventCode, List<SurveyEvent>> eventListMap = aggregateRoot.pollAllEvents();
			eventListMap.forEach((eventCode, eventList) -> {
				for (SurveyEvent event : eventList) {
					eventPublisher.publishEvent(event, eventCode);
				}
			});
		}
	}

	private void registerEvent(AbstractRoot root) {
		if (root instanceof Survey survey) {
			root.setCreateEventId(survey.getSurveyId());
		}
	}
}
