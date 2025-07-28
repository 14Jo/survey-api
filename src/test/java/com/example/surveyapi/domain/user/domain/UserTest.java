package com.example.surveyapi.domain.user.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;

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
        User user = createUser();

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
    @DisplayName("회원 정보 수정 - 성공")
    void update_success() {

        // given
        User user = createUser();

        // when
        user.update("Password124", null,
            null, null, null, null);

        // then
        assertThat(user.getAuth().getPassword()).isEqualTo("Password124");
    }

    @Test
    @DisplayName("회원탈퇴 - 성공")
    void delete_setsIsDeletedTrue() {
        User user = createUser();
        assertThat(user.getIsDeleted()).isFalse();

        user.delete();

        assertThat(user.getIsDeleted()).isTrue();
    }

    private User createUser() {
        String email = "user@example.com";
        String password = "Password123";
        String name = "홍길동";
        LocalDateTime birthDate = LocalDateTime.of(1990, 1, 1, 9, 0);
        Gender gender = Gender.MALE;
        String province = "서울시";
        String district = "강남구";
        String detailAddress = "테헤란로 123";
        String postalCode = "06134";

        User user = User.create(
            email, password,
            name, birthDate, gender,
            province, district,
            detailAddress, postalCode
        );

        return user;
    }
}
