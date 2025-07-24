package com.example.surveyapi.domain.user.domain.user;

import java.time.LocalDateTime;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.enums.Role;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Auth;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "auth", nullable = false, columnDefinition = "jsonb")
    private Auth auth;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "profile", nullable = false, columnDefinition = "jsonb")
    private Profile profile;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "grade", nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    public User(Auth auth , Profile profile) {
        this.auth = auth;
        this.profile = profile;
        this.role = Role.USER;
        this.grade = Grade.LV1;
    }

    public User(
        String email,
        String password,
        String name,
        LocalDateTime birthDate,
        Gender gender,
        String province,
        String district,
        String detailAddress,
        String postalCode){

        this.auth = new Auth(email,password);
        this.profile = new Profile(
            name,
            birthDate,
            gender,
            new Address(province,district,detailAddress,postalCode));

        this.role = Role.USER;
        this.grade = Grade.LV1;
    }

    public static User create(String email,
        String password,
        String name,
        LocalDateTime birthDate,
        Gender gender,
        String province,
        String district,
        String detailAddress,
        String postalCode) {

        return new User(
            email,
            password,
            name,
            birthDate,
            gender,
            province,
            district,
            detailAddress,
            postalCode);
    }

    public void update(
        String password, String name,
        String province, String district,
        String detailAddress, String postalCode) {

        if(password != null){
            this.auth.setPassword(password);
        }

        if(name != null){
            this.profile.setName(name);
        }

        Address address = this.profile.getAddress();
        if (address != null) {
            if(province != null){
                address.setProvince(province);
            }

            if(district != null){
                address.setDistrict(district);
            }

            if(detailAddress != null){
                address.setDetailAddress(detailAddress);
            }

            if(postalCode != null){
                address.setPostalCode(postalCode);
            }
        }

        this.setUpdatedAt(LocalDateTime.now());
    }

    public void delete() {
        this.isDeleted = true;
    }
}
