package com.example.surveyapi.global.event.project;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectMemberAddedEvent {

	private final Long userId;
	private final LocalDateTime periodEnd;
	private final Long projectOwnerId;

}
