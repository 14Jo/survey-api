package com.example.surveyapi.domain.user.application.dtos.request.vo;

import java.time.LocalDateTime;


import com.example.surveyapi.domain.user.domain.user.command.ProfileCommand;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProfileRequest {

    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @NotNull(message = "생년월일은 필수입니다.")
    private LocalDateTime birthDate;

    @NotNull(message = "성별은 필수입니다.")
    private Gender gender;

    @Valid
    @NotNull(message = "주소는 필수입니다.")
    private AddressRequest address;

    public ProfileCommand toCommand() {
        return new ProfileCommand(
            name, birthDate, gender, address.toCommand());
    }
}
