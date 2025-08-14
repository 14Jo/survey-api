package com.example.surveyapi.domain.user.application.client.port;

import java.util.List;

import com.example.surveyapi.domain.user.application.client.response.MyProjectRoleResponse;

public interface ProjectPort {
    List<MyProjectRoleResponse> getProjectMyRole(String authHeader, Long userId);
}
