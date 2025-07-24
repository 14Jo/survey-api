package com.example.surveyapi.domain.user.domain.user.vo;

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

    public void setPassword(String password) {
        this.password = password;
    }
}
