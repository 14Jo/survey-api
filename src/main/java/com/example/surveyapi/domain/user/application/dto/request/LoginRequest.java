package com.example.surveyapi.domain.user.application.dto.request;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String email;
    private String password;
}
