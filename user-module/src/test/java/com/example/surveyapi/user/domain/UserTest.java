package com.example.surveyapi.user.domain;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.surveyapi.user.domain.auth.enums.Provider;
import com.example.surveyapi.user.domain.user.User;
import com.example.surveyapi.user.domain.user.enums.Gender;

public class UserTest {

    @Test
    @DisplayName("회원가입 - 정상 성공")
    void signup_success() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        String name = "홍길동";
        String phoneNumber = "010-1234-5678";
        String nickName = "길동이123";
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
        assertThat(user.getProfile().getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(user.getProfile().getNickName()).isEqualTo(nickName);
        assertThat(user.getDemographics().getBirthDate()).isEqualTo(birthDate);
        assertThat(user.getDemographics().getGender()).isEqualTo(gender);
        assertThat(user.getDemographics().getAddress().getProvince()).isEqualTo(province);
        assertThat(user.getDemographics().getAddress().getDistrict()).isEqualTo(district);
        assertThat(user.getDemographics().getAddress().getDetailAddress()).isEqualTo(detailAddress);
        assertThat(user.getDemographics().getAddress().getPostalCode()).isEqualTo(postalCode);
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void update_success() {

        // given
        User user = createUser();

        // when
        user.update(
            "Password124", null, null, null,
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
        String phoneNumber = "010-1234-5678";
        String nickName = "길동이123";
        LocalDateTime birthDate = LocalDateTime.of(1990, 1, 1, 9, 0);
        Gender gender = Gender.MALE;
        String province = "서울시";
        String district = "강남구";
        String detailAddress = "테헤란로 123";
        String postalCode = "06134";
        Provider provider = Provider.LOCAL;

        User user = User.create(
            email, password,
            name, phoneNumber, nickName,
            birthDate, gender,
            province, district,
            detailAddress, postalCode, provider
        );

        return user;
    }
}
