package com.example.surveyapi.project.application.event;

import com.example.surveyapi.global.event.project.ProjectEvent;

public interface ProjectEventPublisher {
	void convertAndSend(ProjectEvent event);
}
