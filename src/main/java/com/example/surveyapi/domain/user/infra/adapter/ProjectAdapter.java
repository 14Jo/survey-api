package com.example.surveyapi.domain.user.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.client.MyProjectRoleResponse;
import com.example.surveyapi.domain.user.application.client.ProjectPort;
import com.example.surveyapi.global.config.client.project.ProjectApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectAdapter implements ProjectPort {

    private final ProjectApiClient projectApiClient;

    @Override
    public List<MyProjectRoleResponse> getProjectMyRole(String authHeader, Long userId) {
        try {
            log.info("ProjectApiClient 호출 시도");
            return projectApiClient.getProjectMyRole(authHeader, userId);
        } catch (Exception e) {
            log.error("ProjectApiClient 호출 실패", e);  // 여기서 예외 내용을 로그로 확인
            throw e;
        }
    }
}
