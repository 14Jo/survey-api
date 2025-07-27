package com.example.surveyapi.domain.user.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUserResponse {

    private Long memberId;
    private String name;
    private LocalDateTime updatedAt;
    private ProfileResponse profile;

    public static UpdateUserResponse from(
        User user
    ) {
        UpdateUserResponse dto = new UpdateUserResponse();
        ProfileResponse profileDto = new ProfileResponse();
        AddressResponse addressDto = new AddressResponse();

        dto.memberId = user.getId();
        dto.name = user.getProfile().getName();
        dto.updatedAt = user.getUpdatedAt();
        dto.profile = profileDto;

        profileDto.birthDate = user.getProfile().getBirthDate();
        profileDto.gender = user.getProfile().getGender();
        profileDto.address = addressDto;

        addressDto.province = user.getProfile().getAddress().getProvince();
        addressDto.district = user.getProfile().getAddress().getDistrict();
        addressDto.detailAddress = user.getProfile().getAddress().getDetailAddress();
        addressDto.postalCode = user.getProfile().getAddress().getPostalCode();

        return dto;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProfileResponse {

        private LocalDateTime birthDate;
        private Gender gender;
        private AddressResponse address;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class AddressResponse {

        private String province;
        private String district;
        private String detailAddress;
        private String postalCode;
    }

}
