package com.example.surveyapi.domain.project.domain.project;

public interface ProjectRepository {

	void save(Project project);

	boolean existsByNameAndIsDeletedFalse(String name);

}