package com.example.surveyapi.domain.project.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.project.domain.entity.Project;

public interface ProjectRepository extends JpaRepository<Project, Long> {
	boolean existsByName(String name);
}
