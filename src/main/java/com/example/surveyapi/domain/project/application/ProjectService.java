package com.example.surveyapi.domain.project.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateManagerRequest;
import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateManagerResponse;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;
import com.example.surveyapi.domain.project.domain.manager.Manager;
import com.example.surveyapi.domain.project.domain.project.Project;
import com.example.surveyapi.domain.project.domain.project.ProjectRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final EntityManager entityManager;

	@Transactional
	public CreateProjectResponse createProject(CreateProjectRequest request, Long currentUserId) {
		validateDuplicateName(request.getName());

		Project project = Project.create(
			request.getName(),
			request.getDescription(),
			currentUserId,
			request.getPeriodStart(),
			request.getPeriodEnd()
		);
		projectRepository.save(project);

		// TODO: 이벤트 발행

		return CreateProjectResponse.from(project.getId());
	}

	@Transactional(readOnly = true)
	public List<ReadProjectResponse> getMyProjects(Long currentUserId) {
		return projectRepository.findMyProjects(currentUserId);
	}

	@Transactional
	public void updateProject(Long projectId, UpdateProjectRequest request) {
		validateDuplicateName(request.getName());
		Project project = findByIdOrElseThrow(projectId);
		project.updateProject(request.getName(), request.getDescription(), request.getPeriodStart(),
			request.getPeriodEnd());
	}

	@Transactional
	public void updateState(Long projectId, UpdateProjectStateRequest request) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateState(request.getState());
	}

	@Transactional
	public void updateOwner(Long projectId, UpdateProjectOwnerRequest request, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateOwner(currentUserId, request.getNewOwnerId());
	}

	@Transactional
	public void deleteProject(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.softDelete(currentUserId);
		// TODO: 이벤트 발행
	}

	@Transactional
	public CreateManagerResponse createManager(Long projectId, CreateManagerRequest request, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		// TODO: 회원 존재 여부
		Manager manager = project.createManager(currentUserId, request.getUserId());
		// 쓰기 지연 flush 하여 id 생성되도록 강제 flush
		entityManager.flush(); // TODO: 다른 방법이 있는지 더 고민해보기
		return CreateManagerResponse.from(manager.getId());
	}

	@Transactional
	public void updateManagerRole(Long projectId, Long managerId, UpdateManagerRoleRequest request,
		Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateManagerRole(currentUserId, managerId, request.getNewRole());
	}

	private void validateDuplicateName(String name) {
		if (projectRepository.existsByNameAndIsDeletedFalse(name)) {
			throw new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME);
		}
	}

	private Project findByIdOrElseThrow(Long projectId) {
		return projectRepository.findByIdAndIsDeletedFalse(projectId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PROJECT));
	}
}