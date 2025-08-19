package com.example.surveyapi.domain.project.application.event;

public interface ProjectDomainEventPublisher {
	void publish(Object event);
}