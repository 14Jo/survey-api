package com.example.surveyapi.domain.user.domain.user;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.domain.auth.Auth;
import com.example.surveyapi.domain.user.domain.auth.enums.Provider;
import com.example.surveyapi.domain.user.domain.demographics.Demographics;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.enums.Role;
import com.example.surveyapi.global.event.UserWithdrawEvent;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Embedded
    @Column(name = "profile", nullable = false)
    private Profile profile;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "grade", nullable = false)
    @Enumerated(EnumType.STRING)
    private Grade grade;

    @Column(name = "point", nullable = false)
    private int point;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Auth auth;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Demographics demographics;

    @Transient
    private UserWithdrawEvent userWithdrawEvent;

    private User(Profile profile) {
        this.profile = profile;
        this.role = Role.USER;
        this.grade = Grade.BRONZE;
        this.point = 0;
    }

    public void setDemographics(Demographics demographics) {
        this.demographics = demographics;
    }

    public static User create(
        String email, String password,
        String name, String phoneNumber, String nickname,
        LocalDateTime birthDate, Gender gender,
        String province, String district,
        String detailAddress, String postalCode,
        Provider provider

    ) {
        Address address = Address.create(
            province, district,
            detailAddress, postalCode);

        Profile profile = Profile.create(
            name, phoneNumber, nickname);

        User user = new User(profile);

        Auth auth = Auth.create(
            user, email, password,
            provider, null);

        user.auth = auth;

        Demographics demographics = Demographics.create(
            user, birthDate,
            gender, address);

        user.demographics = demographics;

        return user;
    }

    public void update(
        String password, String name,
        String phoneNumber, String nickName,
        String province, String district,
        String detailAddress, String postalCode) {

        this.auth.updateAuth(password);

        this.profile.updateProfile(name,phoneNumber,nickName);

        this.demographics.getAddress().
            updateAddress(province,district,detailAddress,postalCode);

        this.setUpdatedAt(LocalDateTime.now());
    }

    public void registerUserWithdrawEvent() {
        this.userWithdrawEvent = new UserWithdrawEvent(this.id);
    }

    public UserWithdrawEvent getUserWithdrawEvent() {
        if (userWithdrawEvent == null) {
            throw new CustomException(CustomErrorCode.SERVER_ERROR);
        }
        return userWithdrawEvent;
    }

    public void clearUserWithdrawEvent() {
        this.userWithdrawEvent = null;
    }

    public void delete() {
        this.isDeleted = true;
        this.auth.delete();
        this.demographics.delete();

        this.auth.masking();
        this.profile.masking();
        this.demographics.masking();
    }

    public void increasePoint(){
        this.point += 5;
        updatePointGrade();
    }

    private void updatePointGrade(){
        if(this.point >= 100){
            this.point -= 100;
            if(this.grade.next() != null){
                this.grade = this.grade.next();
            }
        }
    }


}
