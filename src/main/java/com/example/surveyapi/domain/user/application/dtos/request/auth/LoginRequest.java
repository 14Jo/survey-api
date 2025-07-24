package com.example.surveyapi.domain.user.application.dtos.request.auth;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String email;
    private String password;

}
