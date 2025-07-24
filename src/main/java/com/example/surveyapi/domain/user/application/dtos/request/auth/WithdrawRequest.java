package com.example.surveyapi.domain.user.application.dtos.request.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class WithdrawRequest {
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}