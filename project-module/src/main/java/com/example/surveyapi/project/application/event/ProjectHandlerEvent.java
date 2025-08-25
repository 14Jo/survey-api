package com.example.surveyapi.project.application.event;

import org.springframework.stereotype.Service;

import com.example.surveyapi.project.application.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectHandlerEvent implements ProjectEventListenerPort {

	private final ProjectService projectService;

	@Override
	public void handleUserWithdrawEvent(Long userId) {
		projectService.handleUserWithdraw(userId);
	}
}
