package com.example.surveyapi.domain.project.application;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.SearchProjectRequest;
import com.example.surveyapi.domain.project.application.dto.response.ProjectInfoResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectManagerInfoResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectMemberIdsResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectMemberInfoResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectSearchInfoResponse;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.repository.ProjectRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectQueryService {

	private final ProjectRepository projectRepository;

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
	public Slice<ProjectSearchInfoResponse> searchProjects(SearchProjectRequest request, Pageable pageable) {

		return projectRepository.searchProjectsNoOffset(request.getKeyword(), request.getLastProjectId(), pageable)
			.map(ProjectSearchInfoResponse::from);
	}

	@Transactional(readOnly = true)
	public ProjectInfoResponse getProject(Long projectId) {
		Project project = findByIdOrElseThrow(projectId);

		return ProjectInfoResponse.from(project);
	}

	@Transactional(readOnly = true)
	public ProjectMemberIdsResponse getProjectMemberIds(Long projectId) {
		Project project = findByIdOrElseThrow(projectId);

		return ProjectMemberIdsResponse.from(project);
	}

	private Project findByIdOrElseThrow(Long projectId) {

		return projectRepository.findByIdAndIsDeletedFalse(projectId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PROJECT));
	}
}
