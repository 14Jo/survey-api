package com.example.surveyapi.domain.project.application;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.domain.project.event.ProjectEventPublisher;
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
	private final ProjectEventPublisher projectEventPublisher;

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
		validateDuplicateName(request.getName());
		Project project = findByIdOrElseThrow(projectId);
		project.updateProject(request.getName(), request.getDescription(), request.getPeriodStart(),
			request.getPeriodEnd());
		publishProjectEvents(project);
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
		publishProjectEvents(project);
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
		publishProjectEvents(project);
	}

	@Transactional
	public void deleteManager(Long projectId, Long managerId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.deleteManager(currentUserId, managerId);
		publishProjectEvents(project);
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
		publishProjectEvents(project);
	}

	@Transactional
	public void leaveProjectMember(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.removeMember(currentUserId);
		publishProjectEvents(project);
	}

	@Scheduled(cron = "0 0 0 * * *") // 매일 00시 실행
	@Transactional
	public void updateProjectStates() {
		LocalDateTime now = LocalDateTime.now();
		updatePendingProjects(now);
		updateInProgressProjects(now);
	}

	private void updatePendingProjects(LocalDateTime now) {
		List<Project> pendingProjects = projectRepository.findPendingProjectsToStart(now);
		List<Long> projectIds = pendingProjects.stream().map(Project::getId).toList();
		projectRepository.updateStateByIds(projectIds, ProjectState.IN_PROGRESS);
	}

	private void updateInProgressProjects(LocalDateTime now) {
		List<Project> inProgressProjects = projectRepository.findInProgressProjectsToClose(now);
		List<Long> projectIds = inProgressProjects.stream().map(Project::getId).toList();
		projectRepository.updateStateByIds(projectIds, ProjectState.CLOSED);
	}

	private void validateDuplicateName(String name) {
		if (projectRepository.existsByNameAndIsDeletedFalse(name)) {
			throw new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME);
		}
	}

	private void publishProjectEvents(Project project) {
		project.pullDomainEvents().forEach(projectEventPublisher::publish);
	}

	private Project findByIdOrElseThrow(Long projectId) {

		return projectRepository.findByIdAndIsDeletedFalse(projectId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PROJECT));
	}
}