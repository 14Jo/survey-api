package com.example.surveyapi.domain.project.infra.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;
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
	public Page<ReadProjectResponse> findMyProjects(Pageable pageable, Long currentUserId) {
		return projectQuerydslRepository.findMyProjects(pageable, currentUserId);
	}
}