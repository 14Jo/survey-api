package com.example.surveyapi.domain.participation.application.dto.request;

import java.util.List;

import lombok.Getter;

@Getter
public class SearchParticipationRequest {
	private List<Long> surveyIds;
}
