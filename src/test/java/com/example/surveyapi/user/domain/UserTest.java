package com.example.surveyapi.user.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.global.exception.CustomException;

public class UserTest {

    @Test
    @DisplayName("회원가입 - 정상 성공")
    void signup_success() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        String name = "홍길동";
        LocalDateTime birthDate = LocalDateTime.of(1990, 1, 1, 9, 0);
        Gender gender = Gender.MALE;
        String province = "서울시";
        String district = "강남구";
        String detailAddress = "테헤란로 123";
        String postalCode = "06134";

        // when
        User user = User.create(
            email, password, name, birthDate, gender,
            province, district, detailAddress, postalCode
        );

        // then
        assertThat(user.getAuth().getEmail()).isEqualTo(email);
        assertThat(user.getAuth().getPassword()).isEqualTo(password);
        assertThat(user.getProfile().getName()).isEqualTo(name);
        assertThat(user.getProfile().getBirthDate()).isEqualTo(birthDate);
        assertThat(user.getProfile().getGender()).isEqualTo(gender);
        assertThat(user.getProfile().getAddress().getProvince()).isEqualTo(province);
        assertThat(user.getProfile().getAddress().getDistrict()).isEqualTo(district);
        assertThat(user.getProfile().getAddress().getDetailAddress()).isEqualTo(detailAddress);
        assertThat(user.getProfile().getAddress().getPostalCode()).isEqualTo(postalCode);
    }

    @Test
    @DisplayName("회원가입 - 실패 (요청값 누락 시)")
    void signup_fail() {
        // given

        // when & then
        assertThatThrownBy(() -> User.create(
            null, null, null, null,
            null, null, null, null, null
        )).isInstanceOf(CustomException.class);
    }
}
