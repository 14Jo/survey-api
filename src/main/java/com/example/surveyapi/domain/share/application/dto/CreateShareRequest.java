package com.example.surveyapi.domain.share.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateShareRequest {
	@NotNull
	private Long surveyId;
}
