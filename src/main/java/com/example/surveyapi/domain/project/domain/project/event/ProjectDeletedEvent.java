package com.example.surveyapi.domain.project.domain.project.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDeletedEvent {

	private final Long projectId;
	private final String projectName;

}