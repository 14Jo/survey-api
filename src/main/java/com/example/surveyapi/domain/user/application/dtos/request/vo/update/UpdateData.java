package com.example.surveyapi.domain.user.application.dtos.request.vo.update;

import com.example.surveyapi.domain.user.application.dtos.request.UpdateRequest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateData {

    private final String password;
    private final String name;
    private final String province;
    private final String district;
    private final String detailAddress;
    private final String postalCode;

    public static UpdateData extractUpdateData(UpdateRequest request) {
        String password = null;
        String name = null;
        String province = null;
        String district = null;
        String detailAddress = null;
        String postalCode = null;

        if (request.getAuth() != null) {
            password = request.getAuth().getPassword();
        }

        if (request.getProfile() != null) {
            name = request.getProfile().getName();

            if (request.getProfile().getAddress() != null) {
                province = request.getProfile().getAddress().getProvince();
                district = request.getProfile().getAddress().getDistrict();
                detailAddress = request.getProfile().getAddress().getDetailAddress();
                postalCode = request.getProfile().getAddress().getPostalCode();
            }
        }

        return new UpdateData(
            password, name,
            province, district,
            detailAddress, postalCode);
    }
}
