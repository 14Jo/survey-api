package com.example.surveyapi.user.domain.demographics.vo;

import com.example.surveyapi.user.domain.util.MaskingUtils;

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

    public static Address create(
        String province, String district,
        String detailAddress, String postalCode
    ) {
        Address address = new Address();
        address.province = province;
        address.district = district;
        address.detailAddress = detailAddress;
        address.postalCode = postalCode;

        return address;
    }

    public void updateAddress(
        String province, String district,
        String detailAddress, String postalCode
    ) {
        if (province != null) {
            this.province = province;
        }

        if (district != null) {
            this.district = district;
        }

        if (detailAddress != null) {
            this.detailAddress = detailAddress;
        }

        if (postalCode != null) {
            this.postalCode = postalCode;
        }
    }

    public void masking() {
        this.district = MaskingUtils.maskDistrict(district);
        this.detailAddress = MaskingUtils.maskDetailAddress(detailAddress);
        this.postalCode = MaskingUtils.maskPostalCode(postalCode);
    }
}
