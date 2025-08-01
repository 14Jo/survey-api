package com.example.surveyapi.domain.share.application.share.dto;

import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateShareRequest {
	@NotNull
	private Long surveyId;
	@NotNull
	private ShareMethod shareMethod;
}
