package com.example.surveyapi.domain.project.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

	private final ProjectRepository projectRepository;

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
		projectRepository.save(project);
	}

	@Transactional
	public void updateState(Long projectId, UpdateProjectStateRequest request) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateState(request.getState());
		projectRepository.save(project);
	}

	@Transactional
	public void updateOwner(Long projectId, UpdateProjectOwnerRequest request, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateOwner(currentUserId, request.getNewOwnerId());
		projectRepository.save(project);
	}

	@Transactional
	public void deleteProject(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.softDelete(currentUserId);
		projectRepository.save(project);
	}

	@Transactional
	public void joinProjectManager(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.addManager(currentUserId);
		projectRepository.save(project);
	}

	@Transactional
	public void updateManagerRole(Long projectId, Long managerId, UpdateManagerRoleRequest request,
		Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.updateManagerRole(currentUserId, managerId, request.getNewRole());
		projectRepository.save(project);
	}

	@Transactional
	public void deleteManager(Long projectId, Long managerId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.deleteManager(currentUserId, managerId);
		projectRepository.save(project);
	}

	@Transactional
	public void joinProjectMember(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.addMember(currentUserId);
		projectRepository.save(project);
	}

	@Transactional
	public void leaveProjectManager(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.removeManager(currentUserId);
		projectRepository.save(project);
	}

	@Transactional
	public void leaveProjectMember(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.removeMember(currentUserId);
		projectRepository.save(project);
	}

	@Transactional
	public void handleUserWithdraw(Long userId) {
		// 카테시안 곱 발생
		List<Project> projects = projectRepository.findAllWithParticipantsByUserId(userId);

		for (Project project : projects) {
			boolean isManager = project.getProjectManagers().stream()
				.anyMatch(m -> m.isSameUser(userId) && !m.getIsDeleted());
			if (isManager) {
				project.removeManager(userId);
			}

			boolean isMember = project.getProjectMembers().stream()
				.anyMatch(m -> m.isSameUser(userId) && !m.getIsDeleted());
			if (isMember) {
				project.removeMember(userId);
			}

			if (project.getOwnerId().equals(userId)) {
				project.softDelete(userId);
			}
		}

		projectRepository.saveAll(projects);
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