package com.example.surveyapi.domain.project.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.project.application.ProjectService;
import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.domain.project.application.dto.response.ReadProjectResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

	private final ProjectService projectService;

	@PostMapping
	public ResponseEntity<ApiResponse<CreateProjectResponse>> create(
		@RequestBody @Valid CreateProjectRequest request,
		@AuthenticationPrincipal Long currentUserId
	) {
		CreateProjectResponse projectId = projectService.create(request, currentUserId);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("프로젝트 생성 성공", projectId));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<List<ReadProjectResponse>>> getMyProjects(
		@AuthenticationPrincipal Long currentUserId
	) {
		List<ReadProjectResponse> result = projectService.getMyProjects(currentUserId);
		return ResponseEntity.ok(ApiResponse.success("나의 프로젝트 목록 조회 성공", result));
	}

	@PutMapping("/{projectId}")
	public ResponseEntity<ApiResponse<String>> update(
		@PathVariable Long projectId,
		@RequestBody @Valid UpdateProjectRequest request
	) {
		projectService.update(projectId, request);
		return ResponseEntity.ok(ApiResponse.success("프로젝트 정보 수정 성공", null));
	}

	@PatchMapping("/{projectId}/state")
	public ResponseEntity<ApiResponse<String>> updateState(
		@PathVariable Long projectId,
		@RequestBody @Valid UpdateProjectStateRequest request
	) {
		projectService.updateState(projectId, request);
		return ResponseEntity.ok(ApiResponse.success("프로젝트 상태 변경 성공", null));
	}
}