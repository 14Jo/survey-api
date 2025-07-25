package com.example.surveyapi.domain.user.application.dto.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UpdateRequest {

    private UpdateAuthRequest auth;
    private UpdateProfileRequest profile;

    @Getter
    public static class UpdateAuthRequest {

        private String password;
    }

    @Getter
    public static class UpdateProfileRequest {
        private String name;

        private UpdateAddressRequest address;
    }

    @Getter
    public static class UpdateAddressRequest {
        private String province;

        private String district;

        private String detailAddress;

        private String postalCode;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateData {

        private String password;
        private String name;
        private String province;
        private String district;
        private String detailAddress;
        private String postalCode;

        public static UpdateData from(UpdateRequest request) {

            UpdateData dto = new UpdateData();

            if (request.getAuth() != null) {
                dto.password = request.getAuth().getPassword();
            }

            if (request.getProfile() != null) {
                dto.name = request.getProfile().getName();

                if (request.getProfile().getAddress() != null) {
                    dto.province = request.getProfile().getAddress().getProvince();
                    dto.district = request.getProfile().getAddress().getDistrict();
                    dto.detailAddress = request.getProfile().getAddress().getDetailAddress();
                    dto.postalCode = request.getProfile().getAddress().getPostalCode();
                }
            }

            return dto;
        }
    }
}
