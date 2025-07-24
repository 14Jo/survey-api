package com.example.surveyapi.domain.user.application.dtos.request.vo.update;

import lombok.Getter;

@Getter
public class UpdateAddressRequest {
    private String province;

    private String district;

    private String detailAddress;

    private String postalCode;
}
