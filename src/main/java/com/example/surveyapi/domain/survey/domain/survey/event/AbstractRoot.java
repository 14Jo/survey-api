package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Transient;

public abstract class AbstractRoot extends BaseEntity {

	@Transient
	private final List<DomainEvent> domainEvents = new ArrayList<>();

	protected void registerEvent(DomainEvent event) {
		this.domainEvents.add(event);
	}

	public List<DomainEvent> pollAllEvents() {
		if (domainEvents.isEmpty()) {
			return Collections.emptyList();
		}
		List<DomainEvent> events = new ArrayList<>(this.domainEvents);
		this.domainEvents.clear();
		return events;
	}

	public void setCreateEventId(Long surveyId) {
		for (DomainEvent event : this.domainEvents) {
			if (event instanceof SurveyCreatedEvent createdEvent) {
				createdEvent.setSurveyId(surveyId);
				break;
			}
		}
	}
}