package com.example.surveyapi.domain.user.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.client.MyProjectRoleResponse;
import com.example.surveyapi.domain.user.application.client.ProjectPort;
import com.example.surveyapi.global.config.client.ExternalApiResponse;
import com.example.surveyapi.global.config.client.project.ProjectApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectAdapter implements ProjectPort {

    private final ProjectApiClient projectApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<MyProjectRoleResponse> getProjectMyRole(String authHeader, Long userId) {

        ExternalApiResponse response = projectApiClient.getProjectMyRole(authHeader, userId);
        Object rawData = response.getOrThrow();

        List<MyProjectRoleResponse> projectMyRoleList =
            objectMapper.convertValue(
                rawData,
                new TypeReference<List<MyProjectRoleResponse>>() {
                }
            );

        return projectMyRoleList;
    }
}
