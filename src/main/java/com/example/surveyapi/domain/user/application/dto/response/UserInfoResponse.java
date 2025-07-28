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
    private String name;
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
        dto.name = user.getProfile().getName();
        dto.role = user.getRole();
        dto.grade = user.getGrade();
        dto.createdAt = user.getCreatedAt();
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
