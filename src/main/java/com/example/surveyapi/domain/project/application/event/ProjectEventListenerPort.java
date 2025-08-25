package com.example.surveyapi.domain.project.application.event;

public interface ProjectEventListenerPort {

	void handleUserWithdrawEvent(Long userId);
}
