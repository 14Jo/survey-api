package com.example.surveyapi.domain.project.infra.project;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.project.application.dto.response.ProjectResponse;
import com.example.surveyapi.domain.project.domain.project.Project;
import com.example.surveyapi.domain.project.domain.project.ProjectRepository;
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
	public List<ProjectResponse> findMyProjects(Long currentUserId) {
		return projectQuerydslRepository.findMyProjects(currentUserId);
	}

	@Override
	public Optional<Project> findByIdAndIsDeletedFalse(Long projectId) {
		return projectJpaRepository.findByIdAndIsDeletedFalse(projectId);
	}
}