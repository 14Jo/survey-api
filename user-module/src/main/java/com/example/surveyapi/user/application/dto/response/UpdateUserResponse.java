package com.example.surveyapi.user.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.user.domain.user.User;
import com.example.surveyapi.user.domain.user.enums.Gender;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateUserResponse {

    private Long memberId;
    private LocalDateTime updatedAt;
    private ProfileResponse profile;

    public static UpdateUserResponse from(
        User user
    ) {
        UpdateUserResponse dto = new UpdateUserResponse();
        ProfileResponse profileDto = new ProfileResponse();
        AddressResponse addressDto = new AddressResponse();

        dto.memberId = user.getId();
        dto.updatedAt = user.getUpdatedAt();
        dto.profile = profileDto;


        profileDto.name = user.getProfile().getName();
        profileDto.phoneNumber = user.getProfile().getPhoneNumber();
        profileDto.nickName = user.getProfile().getNickName();
        profileDto.birthDate = user.getDemographics().getBirthDate();
        profileDto.gender = user.getDemographics().getGender();
        profileDto.address = addressDto;

        addressDto.province = user.getDemographics().getAddress().getProvince();
        addressDto.district = user.getDemographics().getAddress().getDistrict();
        addressDto.detailAddress = user.getDemographics().getAddress().getDetailAddress();
        addressDto.postalCode = user.getDemographics().getAddress().getPostalCode();

        return dto;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ProfileResponse {

        private String name;
        private String phoneNumber;
        private String nickName;
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
