package com.example.surveyapi.domain.survey.application.client;

public interface ProjectPort {

	ProjectValidDto getProjectMembers(Long projectId, Long userId);
	
	ProjectStateDto getProjectState(Long projectId);
}
