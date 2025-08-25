package com.example.surveyapi.survey.infra.adapter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.example.surveyapi.survey.application.client.ProjectPort;
import com.example.surveyapi.survey.application.client.ProjectStateDto;
import com.example.surveyapi.survey.application.client.ProjectValidDto;
import com.example.surveyapi.global.client.ProjectApiClient;
import com.example.surveyapi.global.dto.ExternalApiResponse;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("surveyProjectAdapter")
@RequiredArgsConstructor
public class ProjectAdapter implements ProjectPort {

	private final ProjectApiClient projectClient;

	@Override
	@Cacheable(value = "projectMemberCache", key = "#projectId + '_' + #userId")
	public ProjectValidDto getProjectMembers(String authHeader, Long projectId, Long userId) {
		try {
			log.debug("프로젝트 멤버 조회 시작: projectId={}, userId={}", projectId, userId);
			
			ExternalApiResponse projectMembers = projectClient.getProjectMembers(authHeader);
			log.debug("외부 API 응답 받음: success={}, message={}", projectMembers.isSuccess(), projectMembers.getMessage());
			
			if (!projectMembers.isSuccess()) {
				log.warn("프로젝트 멤버 조회 실패: {}", projectMembers.getMessage());
				throw new CustomException(CustomErrorCode.NOT_FOUND_PROJECT);
			}

			Object rawData = projectMembers.getData();
			log.debug("응답 데이터 타입: {}", rawData != null ? rawData.getClass().getSimpleName() : "null");
			
			if (rawData == null) {
				throw new CustomException(CustomErrorCode.SERVER_ERROR, "외부 API 응답 데이터가 없습니다.");
			}

			List<Map<String, Object>> data = (List<Map<String, Object>>)rawData;
			log.debug("변환된 데이터 크기: {}", data.size());

			List<Integer> projectIds = data.stream()
				.map(map -> {
					Object projectIdObj = map.get("projectId");
					log.debug("프로젝트 ID 객체: {}, 타입: {}", projectIdObj, 
						projectIdObj != null ? projectIdObj.getClass().getSimpleName() : "null");
					return (Integer)projectIdObj;
				})
				.toList();

			log.debug("추출된 프로젝트 IDs: {}", projectIds);
			return ProjectValidDto.of(projectIds, projectId);
			
		} catch (Exception e) {
			log.error("프로젝트 멤버 조회 중 오류: projectId={}, userId={}, error={}", 
				projectId, userId, e.getMessage(), e);
			throw e;
		}
	}

	@Override
	@Cacheable(value = "projectStateCache", key = "#projectId")
	public ProjectStateDto getProjectState(String authHeader, Long projectId) {
		try {
			log.debug("프로젝트 상태 조회 시작: projectId={}", projectId);
			
			ExternalApiResponse projectState = projectClient.getProjectState(authHeader, projectId);
			log.debug("외부 API 응답 받음: success={}, message={}", projectState.isSuccess(), projectState.getMessage());
			
			if (!projectState.isSuccess()) {
				log.warn("프로젝트 상태 조회 실패: {}", projectState.getMessage());
				throw new CustomException(CustomErrorCode.NOT_FOUND_PROJECT);
			}

			Object rawData = projectState.getData();
			log.debug("응답 데이터 타입: {}", rawData != null ? rawData.getClass().getSimpleName() : "null");
			
			if (rawData == null) {
				throw new CustomException(CustomErrorCode.SERVER_ERROR, "외부 API 응답 데이터가 없습니다.");
			}

			Map<String, Object> data = (Map<String, Object>)rawData;
			log.debug("데이터 키들: {}", data.keySet());

			String state = Optional.ofNullable(data.get("state"))
				.filter(stateObj -> stateObj instanceof String)
				.map(stateObj -> (String)stateObj)
				.orElseThrow(() -> new CustomException(CustomErrorCode.SERVER_ERROR,
					"state 필드가 없거나 String 타입이 아닙니다."));

			log.debug("추출된 프로젝트 상태: {}", state);
			return ProjectStateDto.of(state);
			
		} catch (Exception e) {
			log.error("프로젝트 상태 조회 중 오류: projectId={}, error={}", projectId, e.getMessage(), e);
			throw e;
		}
	}
}
