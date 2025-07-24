package com.example.surveyapi.domain.project.application.dto.request;

import com.example.surveyapi.domain.project.domain.manager.enums.ManagerRole;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateManagerRoleRequest {
	@NotNull(message = "변경할 권한을 입력해주세요")
	private ManagerRole newRole;
}