package com.example.surveyapi.domain.user.application.client;

import java.util.List;

public interface ProjectPort {
    List<MyProjectRoleResponse> getProjectMyRole(String authHeader, Long userId);

}
