package com.example.surveyapi.domain.project.domain.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.project.domain.dto.ProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectMemberResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectSearchResult;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;

public interface ProjectRepository {

	void save(Project project);

	boolean existsByNameAndIsDeletedFalse(String name);

	List<ProjectManagerResult> findMyProjectsAsManager(Long currentUserId);

	List<ProjectMemberResult> findMyProjectsAsMember(Long currentUserId);

	Page<ProjectSearchResult> searchProjects(String keyword, Pageable pageable);

	Optional<Project> findByIdAndIsDeletedFalse(Long projectId);

	List<Project> findByStateAndIsDeletedFalse(ProjectState projectState);

	List<Project> findProjectsByMember(Long userId);

	List<Project> findProjectsByManager(Long userId);
}