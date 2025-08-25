package com.example.surveyapi.participation.domain.participation.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.example.surveyapi.participation.domain.participation.enums.Gender;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EqualsAndHashCode
public class ParticipantInfo {

	private LocalDate birth;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	private Region region;

	public static ParticipantInfo of(String birth, Gender gender, Region region) {
		ParticipantInfo participantInfo = new ParticipantInfo();
		participantInfo.birth = LocalDateTime.parse(birth).toLocalDate();
		participantInfo.gender = gender;
		participantInfo.region = region;

		return participantInfo;
	}
}
