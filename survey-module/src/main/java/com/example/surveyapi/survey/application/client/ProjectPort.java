package com.example.surveyapi.survey.application.client;

public interface ProjectPort {

	ProjectValidDto getProjectMembers(String authHeader, Long projectId, Long userId);

	ProjectStateDto getProjectState(String authHeader, Long projectId);
}
