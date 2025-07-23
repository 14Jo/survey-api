package com.example.surveyapi.domain.user.domain.user;

import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.domain.user.command.SignupCommand;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.enums.Role;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Auth;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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

    @Column(name = "auth", nullable = false)
    @Embedded
    private Auth auth;

    @Column(name = "profile", nullable = false)
    @Embedded
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

    public static User create(SignupCommand command, PasswordEncoder passwordEncoder) {
        Address address = Address.create(command);

        Profile profile = Profile.create(command,address);

        Auth auth =  Auth.create(command,passwordEncoder);

        return new User(auth, profile);
    }


}
