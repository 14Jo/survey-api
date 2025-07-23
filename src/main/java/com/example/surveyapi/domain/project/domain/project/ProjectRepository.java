package com.example.surveyapi.domain.project.domain.project;

import java.util.List;

import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;

public interface ProjectRepository {

	void save(Project project);

	boolean existsByNameAndIsDeletedFalse(String name);

	List<ReadProjectResponse> findMyProjects(Long currentUserId);

	Project findByIdOrElseThrow(Long projectId);
}