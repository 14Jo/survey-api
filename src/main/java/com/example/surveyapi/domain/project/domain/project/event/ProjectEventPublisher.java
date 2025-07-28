package com.example.surveyapi.domain.project.domain.project.event;

public interface ProjectEventPublisher {
	void publish(DomainEvent event);
}