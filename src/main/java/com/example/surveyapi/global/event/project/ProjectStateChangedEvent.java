package com.example.surveyapi.global.event.project;

import com.example.surveyapi.global.event.EventCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectStateChangedEvent implements ProjectEvent {
	private final Long projectId;
	private final String projectState;

	@Override
	public EventCode getEventCode() {
		return EventCode.PROJECT_STATE_CHANGED;
	}
}