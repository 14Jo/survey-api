package com.example.surveyapi.domain.user.domain.user.vo;
import com.example.surveyapi.domain.user.domain.user.command.SignupCommand;

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


    public static Address create(SignupCommand command) {
        return new Address(
            command.getProfile().getAddress().getProvince(),
            command.getProfile().getAddress().getDistrict(),
            command.getProfile().getAddress().getDetailAddress(),
            command.getProfile().getAddress().getPostalCode()
        );
    }


}
