package com.example.surveyapi.domain.project.domain.project.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.example.surveyapi.domain.project.domain.dto.ProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectMemberResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectSearchResult;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;

public interface ProjectRepository {

	void save(Project project);

	void saveAll(List<Project> projects);

	boolean existsByNameAndIsDeletedFalse(String name);

	List<ProjectManagerResult> findMyProjectsAsManager(Long currentUserId);

	List<ProjectMemberResult> findMyProjectsAsMember(Long currentUserId);

	Slice<ProjectSearchResult> searchProjectsNoOffset(String keyword, Long lastProjectId, Pageable pageable);

	Optional<Project> findByIdAndIsDeletedFalse(Long projectId);

	List<Project> findPendingProjectsToStart(LocalDateTime now);

	List<Project> findInProgressProjectsToClose(LocalDateTime now);

	void updateStateByIds(List<Long> projectIds, ProjectState newState);

	void removeMemberFromProjects(Long userId);

	void removeManagerFromProjects(Long userId);

	void removeProjects(Long userId);
}