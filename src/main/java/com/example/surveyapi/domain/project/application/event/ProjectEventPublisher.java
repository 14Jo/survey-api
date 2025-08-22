package com.example.surveyapi.domain.project.application.event;

import com.example.surveyapi.global.event.project.ProjectEvent;

public interface ProjectEventPublisher {
	void convertAndSend(ProjectEvent event);
}
