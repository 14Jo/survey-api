package com.example.surveyapi.domain.user.application.dtos.request;

import com.example.surveyapi.domain.user.application.dtos.request.vo.AuthRequest;
import com.example.surveyapi.domain.user.application.dtos.request.vo.ProfileRequest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Valid
    @NotNull(message = "인증 정보는 필수입니다.")
    private AuthRequest auth;

    @Valid
    @NotNull(message = "프로필 정보는 필수입니다.")
    private ProfileRequest profile;

}
