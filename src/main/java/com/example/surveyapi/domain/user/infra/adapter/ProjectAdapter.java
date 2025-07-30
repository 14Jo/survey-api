package com.example.surveyapi.domain.user.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.client.MyProjectRoleResponse;
import com.example.surveyapi.domain.user.application.client.ProjectPort;
import com.example.surveyapi.global.config.client.project.ProjectApiClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectAdapter implements ProjectPort {

    private final ProjectApiClient projectApiClient;

    @Override
    public List<MyProjectRoleResponse> getProjectMyRole(String authHeader, Long userId) {
        return projectApiClient.getProjectMyRole(authHeader,userId);
    }
}
