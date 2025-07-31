package com.example.surveyapi.domain.project.infra.project.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
	boolean existsByNameAndIsDeletedFalse(String name);

	Optional<Project> findByIdAndIsDeletedFalse(Long projectId);

	List<Project> findByStateAndIsDeletedFalse(ProjectState state);
}
