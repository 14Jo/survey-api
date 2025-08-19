package com.example.surveyapi.domain.project.domain.project.event;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;

@MappedSuperclass
public abstract class ProjectAbstractRoot extends BaseEntity {

	@Transient
	private final List<Object> domainEvents = new ArrayList<>();

	// 도메인 메서드(addManager 등)에서 이벤트 적재
	protected void registerEvent(Object event) {
		domainEvents.add(Objects.requireNonNull(event, "requireNonNull"));
	}

	// 이벤트 등록/ 관리
	public List<Object> pullDomainEvents() {
		List<Object> events = new ArrayList<>(domainEvents);
		domainEvents.clear();
		return events;
	}
}