package com.example.surveyapi.domain.project.application.dto.request;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class UpdateProjectRequest {
	private String name;
	private String description;
	private LocalDateTime periodStart;
	private LocalDateTime periodEnd;
}
