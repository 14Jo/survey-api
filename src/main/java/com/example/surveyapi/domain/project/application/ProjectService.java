package com.example.surveyapi.domain.project.application;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.example.surveyapi.domain.project.application.dto.response.ProjectManagerInfoResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectMemberIdsResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectMemberInfoResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectSearchInfoResponse;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.event.ProjectEventPublisher;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

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

	@Transactional(readOnly = true)
	public List<ProjectManagerInfoResponse> getMyProjectsAsManager(Long currentUserId) {

		return projectRepository.findMyProjectsAsManager(currentUserId)
			.stream()
			.map(ProjectManagerInfoResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ProjectMemberInfoResponse> getMyProjectsAsMember(Long currentUserId) {

		return projectRepository.findMyProjectsAsMember(currentUserId)
			.stream()
			.map(ProjectMemberInfoResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public Page<ProjectSearchInfoResponse> searchProjects(String keyword, Pageable pageable) {

		return projectRepository.searchProjects(keyword, pageable)
			.map(ProjectSearchInfoResponse::from);
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
		project.pullDomainEvents().forEach(projectEventPublisher::publish);
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
		project.pullDomainEvents().forEach(projectEventPublisher::publish);
	}

	@Transactional
	public CreateManagerResponse addManager(Long projectId, CreateManagerRequest request, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);

		// TODO: 회원 존재 여부

		project.addManager(currentUserId, request.getUserId());
		projectRepository.save(project);

		return CreateManagerResponse.from(
			project.getProjectManagers().get(project.getProjectManagers().size() - 1).getId());
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
	public void joinProject(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.addMember(currentUserId);
	}

	@Transactional(readOnly = true)
	public ProjectMemberIdsResponse getProjectMemberIds(Long projectId) {
		Project project = findByIdOrElseThrow(projectId);
		return ProjectMemberIdsResponse.from(project);
	}

	@Transactional
	public void leaveProject(Long projectId, Long currentUserId) {
		Project project = findByIdOrElseThrow(projectId);
		project.removeMember(currentUserId);
	}

	private void validateDuplicateName(String name) {
		if (projectRepository.existsByNameAndIsDeletedFalse(name)) {
			throw new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME);
		}
	}

	// TODO: LIST별 fetchJoin 생각
	private Project findByIdOrElseThrow(Long projectId) {

		return projectRepository.findByIdAndIsDeletedFalse(projectId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PROJECT));
	}
}