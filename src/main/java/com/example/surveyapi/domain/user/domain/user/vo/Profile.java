package com.example.surveyapi.domain.user.domain.user.vo;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.domain.user.enums.Gender;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Profile {

    private String name;
    private LocalDateTime birthDate;
    private Gender gender;
    private Address address;


}
