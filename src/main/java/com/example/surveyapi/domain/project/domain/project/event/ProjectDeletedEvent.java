package com.example.surveyapi.domain.project.domain.project.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDeletedEvent implements DomainEvent {

	private final Long projectId;
	private final String projectName;

}