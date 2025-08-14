package com.example.surveyapi.domain.user.application.client.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class MyProjectRoleResponse {
    private Long projectId;
    private String myRole;
}
