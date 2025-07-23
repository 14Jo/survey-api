package com.example.surveyapi.domain.user.application.dtos.response;

import java.time.LocalDateTime;
import java.util.Optional;

import com.example.surveyapi.domain.user.application.dtos.response.vo.AddressResponse;
import com.example.surveyapi.domain.user.application.dtos.response.vo.ProfileResponse;
import com.example.surveyapi.domain.user.domain.user.User;
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

    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getAuth().getEmail(),
            user.getProfile().getName(),
            user.getRole(),
            user.getGrade(),
            user.getCreatedAt(),
            new ProfileResponse(
                user.getProfile().getBirthDate(),
                user.getProfile().getGender(),
                new AddressResponse(
                    user.getProfile().getAddress().getProvince(),
                    user.getProfile().getAddress().getDistrict(),
                    user.getProfile().getAddress().getDetailAddress(),
                    user.getProfile().getAddress().getPostalCode()
                )
            )
        );
    }
}
