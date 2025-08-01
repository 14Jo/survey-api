package com.example.surveyapi.domain.survey.application.client;

public interface ProjectPort {

	ProjectValidDto getProjectMembers(String authHeader, Long projectId, Long userId);

	ProjectStateDto getProjectState(String authHeader, Long projectId);
}
