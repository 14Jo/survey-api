package com.example.surveyapi.domain.user.domain.user.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthCommand {

    private final String email;
    private final String password;

}
