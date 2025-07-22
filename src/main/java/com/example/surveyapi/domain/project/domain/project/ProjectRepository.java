package com.example.surveyapi.domain.project.domain.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;

public interface ProjectRepository {

	void save(Project project);

	boolean existsByNameAndIsDeletedFalse(String name);

	Page<ReadProjectResponse> findMyProjects(Pageable pageable, Long currentUserId);
}