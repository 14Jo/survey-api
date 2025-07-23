package com.example.surveyapi.domain.project.domain.project;

import java.util.List;
import java.util.Optional;

import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;

public interface ProjectRepository {

	void save(Project project);

	boolean existsByNameAndIsDeletedFalse(String name);

	List<ReadProjectResponse> findMyProjects(Long currentUserId);

	Optional<Project> findByIdAndIsDeletedFalse(Long projectId);
}