package com.example.surveyapi.domain.project.application.event;

import com.example.surveyapi.global.model.ProjectEvent;

public interface ProjectEventPublisher {
	void convertAndSend(ProjectEvent event);
}
