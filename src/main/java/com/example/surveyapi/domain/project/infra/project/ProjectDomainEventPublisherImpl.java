package com.example.surveyapi.domain.project.infra.project;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.project.application.event.ProjectDomainEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectDomainEventPublisherImpl implements ProjectDomainEventPublisher {

	private final ApplicationEventPublisher publisher;

	@Override
	public void publish(Object event) {
		publisher.publishEvent(event);
	}
}
