package com.example.surveyapi.domain.participation.application.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class ParticipationGroupRequest {

	@NotEmpty
	private List<Long> surveyIds;
}
