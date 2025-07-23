package com.example.surveyapi.domain.user.application.dtos.response;

import java.util.List;

import com.example.surveyapi.global.util.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserListResponse {

    private final List<UserResponse> content;
    private final PageInfo page;
}
