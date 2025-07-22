package com.example.surveyapi.domain.project.application;

import static com.example.surveyapi.domain.project.domain.project.vo.ProjectPeriod.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.domain.project.Project;
import com.example.surveyapi.domain.project.domain.project.ProjectRepository;
import com.example.surveyapi.domain.project.domain.project.vo.ProjectPeriod;
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
		ProjectPeriod period = toPeriod(request.getPeriodStart(), request.getPeriodEnd());

		Project project = Project.create(
			request.getName(),
			request.getDescription(),
			currentMemberId,
			period
		);
		project.addOwnerManager(currentMemberId);
		projectRepository.save(project);

		// TODO: 이벤트 발행

		return CreateProjectResponse.toDto(project.getId());
	}

	private void validateDuplicateName(String name) {
		if (projectRepository.existsByNameAndIsDeletedFalse(name)) {
			throw new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME);
		}
	}
}