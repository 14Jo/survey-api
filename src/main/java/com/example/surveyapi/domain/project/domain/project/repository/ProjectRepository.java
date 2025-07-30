package com.example.surveyapi.domain.project.domain.project.repository;

import java.util.List;
import java.util.Optional;

import com.example.surveyapi.domain.project.domain.dto.ProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectMemberResult;
import com.example.surveyapi.domain.project.domain.project.entity.Project;

public interface ProjectRepository {

	void save(Project project);

	boolean existsByNameAndIsDeletedFalse(String name);

	List<ProjectManagerResult> findMyProjectsAsManager(Long currentUserId);

	List<ProjectMemberResult> findMyProjectsAsMember(Long currentUserId);

	Optional<Project> findByIdAndIsDeletedFalse(Long projectId);
}