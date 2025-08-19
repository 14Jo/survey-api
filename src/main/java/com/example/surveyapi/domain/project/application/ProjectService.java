package com.example.surveyapi.domain.project.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.application.event.ProjectDomainEventPublisher;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final ProjectDomainEventPublisher publisher;

	@Transactional
	public CreateProjectResponse createProject(CreateProjectRequest request, Long currentUserId) {
		validateDuplicateName(request.getName());

		Project project = Project.create(
			request.getName(),
			request.getDescription(),
			currentUserId,
			request.getMaxMembers(),
			request.getPeriodStart(),
			request.getPeriodEnd()
		);
		projectRepository.save(project);

		return CreateProjectResponse.of(project.getId(), project.getMaxMembers());
	}

	@Transactional
	public void updateProject(Long projectId, UpdateProjectRequest request) {
		Project project = findByIdOrElseThrow(projectId);

		if (request.getName() != null && !request.getName().equals(project.getName())) {
			validateDuplicateName(request.getName());
		}

		project.updateProject(
			request.getName(), request.getDescription(),
			request.getPeriodStart(), request.getPeriodEnd()
		);
	}

	@Transactional
	public void updateState(Long projectId, UpdateProjectStateRequest request) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateState(request.getState());
		publishProjectEvents(project);
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
		publishProjectEvents(project);
	}

	@Transactional
	public void joinProjectManager(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.addManager(currentUserId);
		publishProjectEvents(project);
	}

	@Transactional
	public void updateManagerRole(Long projectId, Long managerId, UpdateManagerRoleRequest request,
		Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateManagerRole(currentUserId, managerId, request.getNewRole());
	}

	@Transactional
	public void deleteManager(Long projectId, Long managerId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.deleteManager(currentUserId, managerId);
	}

	@Transactional
	public void joinProjectMember(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.addMember(currentUserId);
		publishProjectEvents(project);
	}

	@Transactional
	public void leaveProjectManager(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.removeManager(currentUserId);
	}

	@Transactional
	public void leaveProjectMember(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.removeMember(currentUserId);
	}

	private void validateDuplicateName(String name) {
		if (projectRepository.existsByNameAndIsDeletedFalse(name)) {
			throw new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME);
		}
	}

	private void publishProjectEvents(Project project) {
		project.pullDomainEvents().forEach(publisher::publish);
	}

	private Project findByIdOrElseThrow(Long projectId) {

		return projectRepository.findByIdAndIsDeletedFalse(projectId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PROJECT));
	}
}