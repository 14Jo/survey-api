package com.example.surveyapi.project.domain.project.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDeletedDomainEvent {
	private final Long projectId;
	private final String projectName;
	private final Long deleterId;
}
