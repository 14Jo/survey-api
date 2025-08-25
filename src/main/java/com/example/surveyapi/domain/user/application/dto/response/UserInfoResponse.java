package com.example.surveyapi.domain.user.application.dto.response;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.enums.Role;

import lombok.AccessLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfoResponse {

    private Long memberId;
    private String email;
    private Role role;
    private Grade grade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProfileResponse profile;

    public static UserInfoResponse from(
        User user
    ) {
        UserInfoResponse dto = new UserInfoResponse();
        ProfileResponse profileDto = new ProfileResponse();
        AddressResponse addressDto = new AddressResponse();

        dto.memberId = user.getId();
        dto.email = user.getAuth().getEmail();
        dto.role = user.getRole();
        dto.grade = user.getGrade();
        dto.createdAt = user.getCreatedAt();
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
