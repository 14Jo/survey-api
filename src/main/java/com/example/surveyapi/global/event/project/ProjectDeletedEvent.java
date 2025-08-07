package com.example.surveyapi.global.event.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDeletedEvent {

	private final Long projectId;
	private final String projectName;
	private final Long deleterId;

}