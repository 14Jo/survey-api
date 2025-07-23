package com.example.surveyapi.domain.user.domain.user.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Address {

    private String province;
    private String district;
    private String detailAddress;
    private String postalCode;

}
