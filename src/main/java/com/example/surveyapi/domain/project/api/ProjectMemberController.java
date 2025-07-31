package com.example.surveyapi.domain.project.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.project.application.ProjectService;
import com.example.surveyapi.domain.project.application.dto.response.ProjectMemberIdsResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectMemberInfoResponse;
import com.example.surveyapi.global.util.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/projects")
@RequiredArgsConstructor
public class ProjectMemberController {

	private final ProjectService projectService;

	@GetMapping("/me/members")
	public ResponseEntity<ApiResponse<List<ProjectMemberInfoResponse>>> getMyProjectsAsMember(
		@AuthenticationPrincipal Long currentUserId
	) {
		List<ProjectMemberInfoResponse> result = projectService.getMyProjectsAsMember(currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("멤버로 참여한 프로젝트 조회 성공", result));
	}

	@PostMapping("/{projectId}/members")
	public ResponseEntity<ApiResponse<Void>> joinProjectMember(
		@PathVariable Long projectId,
		@AuthenticationPrincipal Long currentUserId
	) {
		projectService.joinProjectMember(projectId, currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 참여 성공"));
	}

	@GetMapping("/{projectId}/members")
	public ResponseEntity<ApiResponse<ProjectMemberIdsResponse>> getProjectMemberIds(
		@PathVariable Long projectId
	) {
		ProjectMemberIdsResponse response = projectService.getProjectMemberIds(projectId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 참여 인원 조회 성공", response));
	}

	@DeleteMapping("/{projectId}/members")
	public ResponseEntity<ApiResponse<Void>> leaveProject(
		@PathVariable Long projectId,
		@AuthenticationPrincipal Long currentUserId
	) {
		projectService.leaveProject(projectId, currentUserId);

		return ResponseEntity.status(HttpStatus.OK)
			.body(ApiResponse.success("프로젝트 탈퇴 성공"));
	}
}