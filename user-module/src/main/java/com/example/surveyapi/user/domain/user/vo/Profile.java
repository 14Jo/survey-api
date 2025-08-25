package com.example.surveyapi.user.domain.user.vo;

import com.example.surveyapi.user.domain.util.MaskingUtils;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Profile {

    private String name;
    private String phoneNumber;
    private String nickName;

    public static Profile create(
        String name, String phoneNumber, String nickName) {
        Profile profile = new Profile();
        profile.name = name;
        profile.phoneNumber = phoneNumber;
        profile.nickName = nickName;

        return profile;
    }

    public void updateProfile(String name, String phoneNumber, String nickName) {
        if (name != null) {
            this.name = name;
        }

        if (phoneNumber != null) {
            this.phoneNumber = phoneNumber;
        }

        if (nickName != null) {
            this.nickName = nickName;
        }
    }

    public void masking() {
        this.name = MaskingUtils.maskName(name);
        this.phoneNumber = MaskingUtils.maskPhoneNumber(phoneNumber);
    }

}
