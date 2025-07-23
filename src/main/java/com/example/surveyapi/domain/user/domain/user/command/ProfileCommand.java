package com.example.surveyapi.domain.user.domain.user.command;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.domain.user.enums.Gender;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileCommand {

    private String name;
    private LocalDateTime birthDate;
    private Gender gender;
    private AddressCommand address;
}
