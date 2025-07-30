package com.example.surveyapi.domain.user.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UpdateUserRequest {

    @Valid
    private UpdateAuthRequest auth;

    @Valid
    private UpdateProfileRequest profile;

    @Getter
    public static class UpdateAuthRequest {
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하이어야 합니다")
        private String password;
    }

    @Getter
    public static class UpdateProfileRequest {
        @Size(max = 20, message = "이름은 최대 20자까지 가능합니다")
        private String name;

        @Valid
        private UpdateAddressRequest address;
    }

    @Getter
    public static class UpdateAddressRequest {

        @Size(max = 50, message = "시/도는 최대 50자까지 가능합니다")
        private String province;

        @Size(max = 50, message = "구/군은 최대 50자까지 가능합니다")
        private String district;

        @Size(max = 100, message = "상세주소는 최대 100자까지 가능합니다")
        private String detailAddress;

        @Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자여야 합니다")
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

        public static UpdateData of(UpdateUserRequest request, String newPassword) {

            UpdateData dto = new UpdateData();

            if (request.getAuth() != null) {
                dto.password = newPassword;
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
