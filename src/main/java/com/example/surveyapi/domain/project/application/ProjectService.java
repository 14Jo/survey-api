package com.example.surveyapi.domain.project.application;

import com.example.surveyapi.domain.project.domain.dto.request.CreateProjectRequest;

public interface ProjectService {
	Long create(CreateProjectRequest request, Long currentMemberId);
}
