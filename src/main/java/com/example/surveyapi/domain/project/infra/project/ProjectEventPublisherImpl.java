package com.example.surveyapi.domain.project.infra.project;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.project.domain.project.event.DomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectEventPublisher;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectEventPublisherImpl implements ProjectEventPublisher {

	private final ApplicationEventPublisher publisher;

	@Override
	public void publish(DomainEvent event) {
		publisher.publishEvent(event);
	}
}
