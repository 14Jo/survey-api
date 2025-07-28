package com.example.surveyapi.domain.user.domain.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.user.domain.auth.Auth;
import com.example.surveyapi.domain.user.domain.demographics.Demographics;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.enums.Role;
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

    public static User create(Profile profile) {
        if (profile == null) {
            throw new CustomException(CustomErrorCode.SERVER_ERROR);
        }
        return new User(profile);
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
}
