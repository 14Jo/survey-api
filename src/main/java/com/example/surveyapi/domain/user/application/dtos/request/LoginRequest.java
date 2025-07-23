package com.example.surveyapi.domain.user.application.dtos.request;

import lombok.Getter;

@Getter
public class LoginRequest {

    private String email;
    private String password;

}
