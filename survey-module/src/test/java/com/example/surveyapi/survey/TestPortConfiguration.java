package com.example.surveyapi.survey;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.example.surveyapi.survey.application.client.ProjectPort;
import com.example.surveyapi.survey.application.client.ProjectStateDto;
import com.example.surveyapi.survey.application.client.ProjectValidDto;

import java.util.List;

@TestConfiguration
public class TestPortConfiguration {

    @Bean
    @Primary
    public ProjectPort mockProjectPort() {
        return new ProjectPort() {
            @Override
            public ProjectValidDto getProjectMembers(String authHeader, Long projectId, Long userId) {
                // 테스트용 프로젝트 멤버십 검증 (항상 성공)
                return ProjectValidDto.of(List.of(userId.intValue()), projectId);
            }

            @Override
            public ProjectStateDto getProjectState(String authHeader, Long projectId) {
                // 테스트용 프로젝트 상태 (항상 활성)
                return ProjectStateDto.of("IN_PROGRESS");
            }
        };
    }
}