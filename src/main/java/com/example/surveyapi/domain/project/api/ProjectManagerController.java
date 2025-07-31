package com.example.surveyapi.domain.project.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.project.application.ProjectService;
import com.example.surveyapi.domain.project.application.dto.request.CreateManagerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateManagerResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectManagerInfoResponse;
import com.example.surveyapi.global.util.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectManagerController {

	private final ProjectService projectService;

	@GetMapping("/me/managers")
	public ResponseEntity<ApiResponse<List<ProjectManagerInfoResponse>>> getMyProjectsAsManager(
		@AuthenticationPrincipal Long currentUserId
	) {
		List<ProjectManagerInfoResponse> result = projectService.getMyProjectsAsManager(currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("담당자로 참여한 프로젝트 조회 성공", result));
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