package com.example.surveyapi.domain.user.application.dto.response;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSnapShotResponse {
    private String birth;
    private Gender gender;
    private Region region;

    public static class Region {
        private String province;
        private String district;
    }

    public static UserSnapShotResponse from(User user) {
        UserSnapShotResponse dto = new UserSnapShotResponse();
        Region regionDto = new Region();

        dto.birth = String.valueOf(user.getProfile().getBirthDate());
        dto.gender = user.getProfile().getGender();
        dto.region = regionDto;

        regionDto.district = user.getProfile().getAddress().getDistrict();
        regionDto.province = user.getProfile().getAddress().getProvince();

        return dto;
    }
}
