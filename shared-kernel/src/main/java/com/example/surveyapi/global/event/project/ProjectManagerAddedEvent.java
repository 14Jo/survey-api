package com.example.surveyapi.global.event.project;

import java.time.LocalDateTime;

import com.example.surveyapi.global.event.EventCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectManagerAddedEvent implements ProjectEvent {

	private final Long userId;
	private final LocalDateTime periodEnd;
	private final Long projectOwnerId;
	private final Long projectId;

	@Override
	public EventCode getEventCode() {
		return EventCode.PROJECT_ADD_MANAGER;
	}
}
