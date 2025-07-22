package com.example.surveyapi.domain.project.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;
import com.example.surveyapi.domain.project.domain.project.Project;
import com.example.surveyapi.domain.project.domain.project.ProjectRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;

	@Transactional
	public CreateProjectResponse create(CreateProjectRequest request, Long currentMemberId) {
		validateDuplicateName(request.getName());

		Project project = Project.create(
			request.getName(),
			request.getDescription(),
			currentMemberId,
			request.getPeriodStart(),
			request.getPeriodEnd()
		);
		projectRepository.save(project);

		// TODO: 이벤트 발행

		return CreateProjectResponse.toDto(project.getId());
	}

	@Transactional(readOnly = true)
	public Page<ReadProjectResponse> getMyProjects(Pageable pageable, Long currentMemberId) {
		return projectRepository.findMyProjects(pageable, currentMemberId);
	}

	private void validateDuplicateName(String name) {
		if (projectRepository.existsByNameAndIsDeletedFalse(name)) {
			throw new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME);
		}
	}
}