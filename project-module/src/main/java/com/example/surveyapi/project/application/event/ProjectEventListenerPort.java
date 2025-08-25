package com.example.surveyapi.project.application.event;

public interface ProjectEventListenerPort {

	void handleUserWithdrawEvent(Long userId);
}
