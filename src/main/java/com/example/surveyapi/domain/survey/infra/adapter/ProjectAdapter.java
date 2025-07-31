package com.example.surveyapi.domain.survey.infra.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.global.config.client.ExternalApiResponse;
import com.example.surveyapi.global.config.client.project.ProjectApiClient;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectAdapter implements ProjectPort {

	private final ProjectApiClient projectClient;

	@Override
	public ProjectValidDto getProjectMembers(Long projectId, Long userId) {
		ExternalApiResponse projectMembers = projectClient.getProjectMembers(projectId);
		if (!projectMembers.isSuccess())
			throw new CustomException(CustomErrorCode.NOT_FOUND_PROJECT);

		Object rawData = projectMembers.getData();
		if (rawData == null) {
			throw new CustomException(CustomErrorCode.SERVER_ERROR, "외부 API 응답 데이터가 없습니다.");
		}

		Map<String, Object> data = (Map<String, Object>)rawData;

		@SuppressWarnings("unchecked")
		List<Long> memberIds = Optional.ofNullable(data.get("memberIds"))
			.filter(memberIdsObj -> memberIdsObj instanceof List)
			.map(memberIdsObj -> (List<Long>)memberIdsObj)
			.orElseThrow(() -> new CustomException(CustomErrorCode.SERVER_ERROR,
				"memberIds 필드가 없거나 List 타입이 아닙니다."));

		return ProjectValidDto.of(memberIds, userId);
	}
}
