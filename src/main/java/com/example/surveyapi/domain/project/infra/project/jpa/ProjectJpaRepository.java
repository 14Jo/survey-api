package com.example.surveyapi.domain.project.infra.project.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.project.domain.project.entity.Project;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
	boolean existsByNameAndIsDeletedFalse(String name);
}
