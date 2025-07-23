package com.example.surveyapi.domain.project.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.project.application.ProjectService;
import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
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
	public ResponseEntity<ApiResponse<CreateProjectResponse>> create(@RequestBody @Valid CreateProjectRequest request) {
		Long currentUserId = 1L; // TODO: 시큐리티 구현 시 변경
		CreateProjectResponse projectId = projectService.create(request, currentUserId);
		return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("프로젝트 생성 성공", projectId));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<List<ReadProjectResponse>>> getMyProjects() {
		Long currentUserId = 1L; // TODO: 시큐리티 구현 시 변경
		List<ReadProjectResponse> result = projectService.getMyProjects(currentUserId);
		return ResponseEntity.ok(ApiResponse.success("나의 프로젝트 목록 조회 성공", result));
	}
}
