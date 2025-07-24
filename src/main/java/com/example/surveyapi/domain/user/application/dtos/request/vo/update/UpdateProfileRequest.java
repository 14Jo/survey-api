package com.example.surveyapi.domain.user.application.dtos.request.vo.update;

import lombok.Getter;

@Getter
public class UpdateProfileRequest {
    private String name;

    private UpdateAddressRequest address;
}
