package com.example.surveyapi.project.domain.project.event;

import com.example.surveyapi.project.domain.project.enums.ProjectState;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectStateChangedDomainEvent {
	private final Long projectId;
	private final ProjectState projectState;
}
