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

    public static UserSnapShotResponse from(User user) {
        UserSnapShotResponse dto = new UserSnapShotResponse();

        dto.birth = String.valueOf(user.getDemographics().getBirthDate());
        dto.gender = user.getDemographics().getGender();
        dto.region = Region.from(user);

        return dto;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Region {
        private String province;
        private String district;

        public static Region from(User user) {
            Region dto = new Region();

            dto.district = user.getDemographics().getAddress().getDistrict();
            dto.province = user.getDemographics().getAddress().getProvince();

            return dto;
        }
    }


}
