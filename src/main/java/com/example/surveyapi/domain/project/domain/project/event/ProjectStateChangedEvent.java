package com.example.surveyapi.domain.project.domain.project.event;

import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectStateChangedEvent {

	private final Long projectId;
	private final ProjectState newState;

}