package com.example.surveyapi.domain.user.application.dtos.response.vo;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.domain.user.enums.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileResponse {

    private final LocalDateTime birthDate;
    private final Gender gender;
    private final AddressResponse address;
}
