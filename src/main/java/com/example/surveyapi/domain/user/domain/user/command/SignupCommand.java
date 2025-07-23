package com.example.surveyapi.domain.user.domain.user.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupCommand {

    private final AuthCommand auth;
    private final ProfileCommand profile;
}
