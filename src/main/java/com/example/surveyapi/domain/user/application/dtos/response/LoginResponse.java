package com.example.surveyapi.domain.user.application.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private MemberResponse member;

    public static LoginResponse of(String token, MemberResponse member) {
        return new LoginResponse(token, member);
    }
}
