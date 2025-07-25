package com.example.surveyapi.domain.user.application.dto.response;

import com.example.surveyapi.domain.user.domain.user.User;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SignupResponse {

    private Long memberId;
    private String email;
    private String name;

    public static SignupResponse from(
        User user
    ){
        SignupResponse dto = new SignupResponse();

        dto.memberId = user.getId();
        dto.email = user.getAuth().getEmail();
        dto.name = user.getProfile().getName();

        return dto;
    }
}
