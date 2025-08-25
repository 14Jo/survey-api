package com.example.surveyapi.project.infra.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.project.domain.project.entity.Project;

public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
	boolean existsByNameAndIsDeletedFalse(String name);
}
