package com.example.surveyapi.domain.survey;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.domain.survey.application.client.ParticipationCountDto;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.SyncTaskExecutor;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

@TestConfiguration
public class TestPortConfiguration {

	@Bean
	@Primary
	public ProjectPort testProjectPort() {
		return new ProjectPort() {
			@Override
			public ProjectValidDto getProjectMembers(String authHeader, Long projectId, Long userId) {
				return ProjectValidDto.of(List.of(1, 2, 3), projectId);
			}

			@Override
			public ProjectStateDto getProjectState(String authHeader, Long projectId) {
				return ProjectStateDto.of("IN_PROGRESS");
			}
		};
	}

	@Bean
	@Primary
	public ParticipationPort testParticipationPort() {
		return new ParticipationPort() {
			@Override
			public ParticipationCountDto getParticipationCounts(List<Long> surveyIds) {
				Map<String, Integer> counts = Map.of(
					"1", 5,
					"2", 10,
					"3", 15
				);
				return ParticipationCountDto.of(counts);
			}
		};
	}

	@Bean
	@Primary
	public Executor testTaskExecutor() {
		return new SyncTaskExecutor();
	}
}