package com.example.surveyapi.user.application.dto.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserByEmailResponse {
    private Long userId;

    public static UserByEmailResponse from(Long userId) {
        UserByEmailResponse dto = new UserByEmailResponse();
        dto.userId = userId;

        return dto;
    }
}
