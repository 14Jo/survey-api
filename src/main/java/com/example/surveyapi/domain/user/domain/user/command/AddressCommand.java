package com.example.surveyapi.domain.user.domain.user.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddressCommand {

    private final String province;
    private final String district;
    private final String detailAddress;
    private final String postalCode;
}
