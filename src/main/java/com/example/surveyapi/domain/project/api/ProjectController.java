package com.example.surveyapi.domain.project.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.project.application.ProjectService;
import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectInfoResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectSearchInfoResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@PostMapping
	public ResponseEntity<ApiResponse<CreateProjectResponse>> createProject(
		@Valid @RequestBody CreateProjectRequest request,
		@AuthenticationPrincipal Long currentUserId
	) {
		CreateProjectResponse projectId = projectService.createProject(request, currentUserId);

		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.success("프로젝트 생성 성공", projectId));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<Page<ProjectSearchInfoResponse>>> searchProjects(
		@RequestParam(required = false) String keyword,
		Pageable pageable
	) {
		Page<ProjectSearchInfoResponse> response = projectService.searchProjects(keyword, pageable);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 검색 성공", response));
	}

	@GetMapping("/{projectId}")
	public ResponseEntity<ApiResponse<ProjectInfoResponse>> getProject(
		@PathVariable Long projectId
	) {
		ProjectInfoResponse response = projectService.getProject(projectId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 상세정보 조회", response));
	}

	@PutMapping("/{projectId}")
	public ResponseEntity<ApiResponse<Void>> updateProject(
		@PathVariable Long projectId,
		@Valid @RequestBody UpdateProjectRequest request
	) {
		projectService.updateProject(projectId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 정보 수정 성공"));
	}

	@PatchMapping("/{projectId}/state")
	public ResponseEntity<ApiResponse<Void>> updateState(
		@PathVariable Long projectId,
		@Valid @RequestBody UpdateProjectStateRequest request
	) {
		projectService.updateState(projectId, request);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 상태 변경 성공"));
	}

	@PatchMapping("/{projectId}/owner")
	public ResponseEntity<ApiResponse<Void>> updateOwner(
		@PathVariable Long projectId,
		@Valid @RequestBody UpdateProjectOwnerRequest request,
		@AuthenticationPrincipal Long currentUserId
	) {
		projectService.updateOwner(projectId, request, currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 소유자 위임 성공"));
	}

	@DeleteMapping("/{projectId}")
	public ResponseEntity<ApiResponse<Void>> deleteProject(
		@PathVariable Long projectId,
		@AuthenticationPrincipal Long currentUserId
	) {
		projectService.deleteProject(projectId, currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 삭제 성공"));
	}
}