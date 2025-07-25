package com.example.surveyapi.domain.user.application.dto.response;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Role;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private MemberResponse member;

    public static LoginResponse of(
        String token, MemberResponse member
    ) {
        return new LoginResponse(token, member);
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MemberResponse {

        private Long memberId;
        private String email;
        private String name;
        private Role role;

        public static MemberResponse from(
            User user
        ){
            MemberResponse dto = new MemberResponse();

            dto.memberId = user.getId();
            dto.email = user.getAuth().getEmail();
            dto.name = user.getProfile().getName();
            dto.role = user.getRole();

            return dto;
        }
    }
}
