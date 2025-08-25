package com.example.surveyapi.project.application.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.project.domain.project.event.ProjectCreatedDomainEvent;
import com.example.surveyapi.project.domain.project.event.ProjectDeletedDomainEvent;
import com.example.surveyapi.project.domain.project.event.ProjectManagerAddedDomainEvent;
import com.example.surveyapi.project.domain.project.event.ProjectMemberAddedDomainEvent;
import com.example.surveyapi.project.domain.project.event.ProjectStateChangedDomainEvent;

import com.example.surveyapi.global.event.project.ProjectCreatedEvent;
import com.example.surveyapi.global.event.project.ProjectDeletedEvent;
import com.example.surveyapi.global.event.project.ProjectStateChangedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventListener {

	private final ProjectEventPublisher projectEventPublisher;
	private final ObjectMapper objectMapper;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProjectCreated(ProjectCreatedDomainEvent internalEvent) {
		ProjectCreatedEvent globalEvent = objectMapper.convertValue(internalEvent, ProjectCreatedEvent.class);
		projectEventPublisher.convertAndSend(globalEvent);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProjectStateChanged(ProjectStateChangedDomainEvent internalEvent) {
		ProjectStateChangedEvent globalEvent = objectMapper.convertValue(internalEvent, ProjectStateChangedEvent.class);
		projectEventPublisher.convertAndSend(globalEvent);
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProjectDeleted(ProjectDeletedDomainEvent internalEvent) {
		ProjectDeletedEvent globalEvent = objectMapper.convertValue(internalEvent, ProjectDeletedEvent.class);
		projectEventPublisher.convertAndSend(globalEvent);
	}
}
