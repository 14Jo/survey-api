package com.example.surveyapi.domain.user.domain.user.vo;

import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.domain.user.command.SignupCommand;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Auth {

    private String email;
    private String password;

    public static Auth create(SignupCommand command, PasswordEncoder passwordEncoder){
        return new Auth(
            command.getAuth().getEmail(),
            passwordEncoder.encode(command.getAuth().getPassword()));
    }
}
