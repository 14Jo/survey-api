package com.example.surveyapi.domain.user.domain.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.user.domain.auth.Auth;
import com.example.surveyapi.domain.user.domain.auth.enums.Provider;
import com.example.surveyapi.domain.user.domain.demographics.Demographics;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.enums.Role;
import com.example.surveyapi.domain.user.domain.user.event.UserWithdrawEvent;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile", nullable = false, columnDefinition = "jsonb")
    private Profile profile;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "grade", nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Auth auth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Demographics demographics;

    @Transient
    private UserWithdrawEvent userWithdrawEvent;


    private User(Profile profile) {
        this.profile = profile;
        this.role = Role.USER;
        this.grade = Grade.LV1;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public void setDemographics(Demographics demographics) {
        this.demographics = demographics;
    }

    public static User create(
        String email, String password,
        String name, LocalDateTime birthDate, Gender gender,
        String province, String district,
        String detailAddress, String postalCode
    ) {
        Address address = Address.of(
            province, district,
            detailAddress, postalCode);

        Profile profile = Profile.of(
            name, birthDate,
            gender, address);

        User user = new User(profile);

        Auth auth = Auth.create(
            user, email, password,
            Provider.LOCAL, null);

        user.auth = auth;

        Demographics demographics = Demographics.create(
            user, birthDate,
            gender, address);

        user.demographics = demographics;

        return user;
    }

    public void update(
        String password, String name,
        String province, String district,
        String detailAddress, String postalCode) {

        if (password != null) {
            this.auth.setPassword(password);
        }

        if (name != null) {
            this.profile.setName(name);
        }

        Address address = this.profile.getAddress();
        if (address != null) {
            if (province != null) {
                address.setProvince(province);
            }

            if (district != null) {
                address.setDistrict(district);
            }

            if (detailAddress != null) {
                address.setDetailAddress(detailAddress);
            }

            if (postalCode != null) {
                address.setPostalCode(postalCode);
            }
        }

        this.setUpdatedAt(LocalDateTime.now());
    }

    public void registerUserWithdrawEvent() {
        this.userWithdrawEvent = new UserWithdrawEvent(this.id);
    }

    public UserWithdrawEvent getUserWithdrawEvent() {
        if(userWithdrawEvent == null){
            throw new CustomException(CustomErrorCode.SERVER_ERROR);
        }
        return userWithdrawEvent;
    }

    public void clearUserWithdrawEvent() {
        this.userWithdrawEvent = null;
    }
}
