package com.example.surveyapi.domain.user.application.dtos.response.select;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.surveyapi.domain.user.application.dtos.response.UserResponse;
import com.example.surveyapi.global.util.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserListResponse {

    private final List<UserResponse> content;
    private final PageInfo page;

    public static UserListResponse from(Page<UserResponse> users){
        return new UserListResponse(
            users.getContent(),
            new PageInfo(
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages()));
    }
}
