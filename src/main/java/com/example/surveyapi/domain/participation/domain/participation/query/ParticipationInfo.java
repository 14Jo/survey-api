package com.example.surveyapi.domain.participation.domain.participation.query;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationInfo {

	private Long participationId;
	private Long surveyId;
	private LocalDateTime participatedAt;
}
