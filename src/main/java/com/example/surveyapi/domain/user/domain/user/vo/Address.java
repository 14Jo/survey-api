package com.example.surveyapi.domain.user.domain.user.vo;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Address {

    private String province;
    private String district;
    private String detailAddress;
    private String postalCode;

    public static Address of(
        String province, String district,
        String detailAddress, String postalCode
    ) {
        return new Address(
            province, district,
            detailAddress, postalCode);
    }
}
