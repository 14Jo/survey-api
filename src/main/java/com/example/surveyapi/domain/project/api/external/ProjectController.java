package com.example.surveyapi.domain.project.api.external;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.project.application.ProjectService;
import com.example.surveyapi.domain.project.application.dto.request.CreateManagerRequest;
import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateManagerResponse;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectInfoResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
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

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<List<ProjectInfoResponse>>> getMyProjects(
		@AuthenticationPrincipal Long currentUserId
	) {
		List<ProjectInfoResponse> result = projectService.getMyProjects(currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("나의 프로젝트 목록 조회 성공", result));
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

	@PostMapping("/{projectId}/managers")
	public ResponseEntity<ApiResponse<CreateManagerResponse>> addManager(
		@PathVariable Long projectId,
		@Valid @RequestBody CreateManagerRequest request,
		@AuthenticationPrincipal Long currentUserId
	) {
		CreateManagerResponse response = projectService.addManager(projectId, request, currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("협력자 추가 성공", response));
	}

	@PatchMapping("/{projectId}/managers/{managerId}/role")
	public ResponseEntity<ApiResponse<Void>> updateManagerRole(
		@PathVariable Long projectId,
		@PathVariable Long managerId,
		@Valid @RequestBody UpdateManagerRoleRequest request,
		@AuthenticationPrincipal Long currentUserId
	) {
		projectService.updateManagerRole(projectId, managerId, request, currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("협력자 권한 수정 성공"));
	}

	@DeleteMapping("/{projectId}/managers/{managerId}")
	public ResponseEntity<ApiResponse<Void>> deleteManager(
		@PathVariable Long projectId,
		@PathVariable Long managerId,
		@AuthenticationPrincipal Long currentUserId
	) {
		projectService.deleteManager(projectId, managerId, currentUserId);
		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("협력자 삭제 성공"));
	}
}