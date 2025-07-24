package com.example.surveyapi.domain.user.application.dtos.response.auth;

import com.example.surveyapi.domain.user.domain.user.User;

import lombok.Getter;

@Getter
public class SignupResponse {
    private Long memberId;
    private String email;
    private String name;

    public SignupResponse(User user){
        this.memberId = user.getId();
        this.email = user.getAuth().getEmail();
        this.name = user.getProfile().getName();
    }

    public static SignupResponse from(User user){
        return new SignupResponse(user);
    }
}
