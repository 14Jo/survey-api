package com.example.surveyapi.user.application.dto.response;

import com.example.surveyapi.user.domain.user.User;
import com.example.surveyapi.user.domain.user.enums.Role;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private MemberResponse member;

    public static LoginResponse of(
        String accessToken, String refreshToken, User user
    ) {
        LoginResponse dto = new LoginResponse();
        dto.accessToken = accessToken;
        dto.refreshToken = refreshToken;
        dto.member = MemberResponse.from(user);

        return dto;
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
        ) {
            MemberResponse dto = new MemberResponse();

            dto.memberId = user.getId();
            dto.email = user.getAuth().getEmail();
            dto.name = user.getProfile().getName();
            dto.role = user.getRole();

            return dto;
        }
    }
}
