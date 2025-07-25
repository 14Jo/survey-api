package com.example.surveyapi.domain.project.domain.project;

import java.util.List;
import java.util.Optional;

import com.example.surveyapi.domain.project.domain.dto.ProjectResult;

public interface ProjectRepository {

	void save(Project project);

	boolean existsByNameAndIsDeletedFalse(String name);

	List<ProjectResult> findMyProjects(Long currentUserId);

	Optional<Project> findByIdAndIsDeletedFalse(Long projectId);
}