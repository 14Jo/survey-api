package com.example.surveyapi.domain.survey;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.List;
import java.util.concurrent.Executor;

@TestConfiguration
public class TestPortConfiguration {

	@Bean
	@Primary
	public ProjectPort testProjectPort() {
		return new ProjectPort() {
			@Override
			public ProjectValidDto getProjectMembers(String authHeader, Long userId, Long projectId) {
				return ProjectValidDto.of(List.of(1, 2, 3), 1L);
			}

			@Override
			public ProjectStateDto getProjectState(String authHeader, Long projectId) {
				return ProjectStateDto.of("IN_PROGRESS");
			}
		};
	}
}