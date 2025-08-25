package com.example.surveyapi.user.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserWithdrawRequest {
    @NotBlank(message = "비밀번호는 필수입니다")
    private String password;
}