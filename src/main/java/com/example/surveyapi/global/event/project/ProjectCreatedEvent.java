package com.example.surveyapi.global.event.project;

import java.time.LocalDateTime;

import com.example.surveyapi.global.event.EventCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectCreatedEvent implements ProjectEvent {
	private Long projectId;
	private Long ownerId;
	private LocalDateTime periodEnd;

	@Override
	public EventCode getEventCode() {
		return EventCode.PROJECT_CREATED;
	}
}
