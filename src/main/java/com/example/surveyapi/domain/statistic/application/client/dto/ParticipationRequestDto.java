package com.example.surveyapi.domain.statistic.application.client.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ParticipationRequestDto {
	List<Long> surveyIds;
}
