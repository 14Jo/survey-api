package com.example.surveyapi.global.event.project;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectStateChangedEvent {

	private final Long projectId;
	private final String newState;

}