package com.example.surveyapi.domain.user.application.dtos.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.application.dtos.response.vo.ProfileResponse;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserResponse {
    private final Long memberId;
    private final String email;
    private final String name;
    private final Role role;
    private final Grade grade;
    private final LocalDateTime createdAt;
    private final ProfileResponse profile;
}
