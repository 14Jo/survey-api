package com.example.surveyapi.domain.project.application.event;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.project.domain.project.event.ProjectDeletedDomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectManagerAddedDomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectMemberAddedDomainEvent;
import com.example.surveyapi.domain.project.domain.project.event.ProjectStateChangedDomainEvent;
import com.example.surveyapi.global.event.project.ProjectDeletedEvent;
import com.example.surveyapi.global.event.project.ProjectManagerAddedEvent;
import com.example.surveyapi.global.event.project.ProjectMemberAddedEvent;
import com.example.surveyapi.global.event.project.ProjectStateChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectEventListener {

	private final ProjectEventPublisher projectEventPublisher;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProjectStateChanged(ProjectStateChangedDomainEvent internalEvent) {
		projectEventPublisher.convertAndSend(new ProjectStateChangedEvent(
			internalEvent.getProjectId(),
			internalEvent.getProjectState().name()
		));
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleProjectDeleted(ProjectDeletedDomainEvent internalEvent) {
		projectEventPublisher.convertAndSend(new ProjectDeletedEvent(
			internalEvent.getProjectId(),
			internalEvent.getProjectName(),
			internalEvent.getDeleterId()
		));
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleManagerAdded(ProjectManagerAddedDomainEvent internalEvent) {
		projectEventPublisher.convertAndSend(new ProjectManagerAddedEvent(
			internalEvent.getUserId(),
			internalEvent.getPeriodEnd(),
			internalEvent.getProjectOwnerId(),
			internalEvent.getProjectId()
		));
	}

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleMemberAdded(ProjectMemberAddedDomainEvent internalEvent) {
		projectEventPublisher.convertAndSend(new ProjectMemberAddedEvent(
			internalEvent.getUserId(),
			internalEvent.getPeriodEnd(),
			internalEvent.getProjectOwnerId(),
			internalEvent.getProjectId()
		));
	}
}
