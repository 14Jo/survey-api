package com.example.surveyapi.domain.project.infra.project;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.project.domain.dto.ProjectManagerResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectMemberResult;
import com.example.surveyapi.domain.project.domain.dto.ProjectSearchResult;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.domain.project.infra.project.jpa.ProjectJpaRepository;
import com.example.surveyapi.domain.project.infra.project.querydsl.ProjectQuerydslRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProjectRepositoryImpl implements ProjectRepository {

	private final ProjectJpaRepository projectJpaRepository;
	private final ProjectQuerydslRepository projectQuerydslRepository;

	@Override
	public void save(Project project) {
		projectJpaRepository.save(project);
	}

	@Override
	public boolean existsByNameAndIsDeletedFalse(String name) {
		return projectJpaRepository.existsByNameAndIsDeletedFalse(name);
	}

	@Override
	public List<ProjectManagerResult> findMyProjectsAsManager(Long currentUserId) {
		return projectQuerydslRepository.findMyProjectsAsManager(currentUserId);
	}

	@Override
	public List<ProjectMemberResult> findMyProjectsAsMember(Long currentUserId) {
		return projectQuerydslRepository.findMyProjectsAsMember(currentUserId);
	}

	@Override
	public Slice<ProjectSearchResult> searchProjectsNoOffset(String keyword, Long lastProjectId, Pageable pageable) {
		return projectQuerydslRepository.searchProjectsNoOffset(keyword, lastProjectId, pageable);
	}

	@Override
	public Optional<Project> findByIdAndIsDeletedFalse(Long projectId) {
		return projectQuerydslRepository.findByIdAndIsDeletedFalse(projectId);
	}

	@Override
	public List<Project> findPendingProjectsToStart(LocalDateTime now) {
		return projectQuerydslRepository.findPendingProjectsToStart(now);
	}

	@Override
	public List<Project> findInProgressProjectsToClose(LocalDateTime now) {
		return projectQuerydslRepository.findInProgressProjectsToClose(now);
	}

	@Override
	public void updateStateByIds(List<Long> projectIds, ProjectState newState) {
		projectQuerydslRepository.updateStateByIds(projectIds, newState);
	}

	@Override
	public void removeMemberFromProjects(Long userId) {
		projectQuerydslRepository.removeMemberFromProjects(userId);
	}

	@Override
	public void removeManagerFromProjects(Long userId) {
		projectQuerydslRepository.removeManagerFromProjects(userId);
	}

	@Override
	public void removeProjects(Long userId) {
		projectQuerydslRepository.removeProjects(userId);
	}
}