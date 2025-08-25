package com.example.surveyapi.project.application.dto.request;

import com.example.surveyapi.project.domain.participant.manager.enums.ManagerRole;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UpdateManagerRoleRequest {
	@NotNull(message = "변경할 권한을 입력해주세요")
	private ManagerRole newRole;
}