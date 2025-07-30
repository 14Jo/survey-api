package com.example.surveyapi.domain.participation.domain.participation.vo;

import java.time.LocalDate;

import com.example.surveyapi.domain.participation.domain.participation.enums.Gender;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class ParticipantInfo {

	private LocalDate birth;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private String region;

	public ParticipantInfo(LocalDate birth, Gender gender, String region) {
		this.birth = birth;
		this.gender = gender;
		this.region = region;
	}
}
