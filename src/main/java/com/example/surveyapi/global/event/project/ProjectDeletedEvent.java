package com.example.surveyapi.global.event.project;

import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.ProjectEvent;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectDeletedEvent implements ProjectEvent {

	private final Long projectId;
	private final String projectName;
	private final Long deleterId;

	@Override
	public EventCode getEventCode() {
		return EventCode.PROJECT_DELETED;
	}
}