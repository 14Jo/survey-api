package com.example.surveyapi.domain.survey;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.util.List;

/**
 * 테스트 환경에서 외부 서비스(Project) 의존성을 대체하기 위한 Stub 설정
 */
@TestConfiguration
public class TestPortConfiguration {

	@Bean
	@Primary // 실제 ProjectPort Bean 대신 테스트용 Bean을 우선적으로 사용
	public ProjectPort testProjectPort() {
		return new ProjectPort() {
			@Override
			public ProjectValidDto getProjectMembers(String authHeader, Long userId, Long projectId) {
				// 테스트 시 권한 검증을 통과시키기 위해 항상 유효한 멤버 목록을 반환하도록 설정
				return ProjectValidDto.of(List.of(1, 2, 3), 1L);
			}

			@Override
			public ProjectStateDto getProjectState(String authHeader, Long projectId) {
				// 테스트 시 프로젝트 상태가 항상 진행 중이라고 가정
				return ProjectStateDto.of("IN_PROGRESS");
			}
		};
	}
}