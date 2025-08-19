package com.example.surveyapi.domain.project.domain.project.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.AfterDomainEventPublication;
import org.springframework.data.domain.DomainEvents;
import org.springframework.util.Assert;

import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public abstract class ProjectAbstractRoot extends BaseEntity {

	@Transient
	private final List<Object> domainEvents = new ArrayList<>();

	protected <T> void registerEvent(T event) {
		Assert.notNull(event, "Domain event must not be null");
		this.domainEvents.add(event);
	}

	@AfterDomainEventPublication
	protected void clearDomainEvents() {
		this.domainEvents.clear();
	}

	@DomainEvents
	protected Collection<Object> domainEvents() {
		return Collections.unmodifiableList(this.domainEvents);
	}
}