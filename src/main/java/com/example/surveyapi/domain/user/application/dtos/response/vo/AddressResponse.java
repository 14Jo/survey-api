package com.example.surveyapi.domain.user.application.dtos.response.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressResponse {

    private final String province;
    private final String district;
    private final String detailAddress;
    private final String postalCode;
}
