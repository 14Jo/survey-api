package com.example.surveyapi.domain.user.application.dtos.response.auth;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private Long memberId;
    private String email;
    private String name;
    private Role role;

    public static MemberResponse from(User user){
        return new MemberResponse(
            user.getId(),
            user.getAuth().getEmail(),
            user.getProfile().getName(),
            user.getRole()
        );
    }
}
