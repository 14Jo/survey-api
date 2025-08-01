package com.example.surveyapi.domain.share.application.share.dto;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateShareRequest {
	@NotNull
	private ShareSourceType sourceType;
	@NotNull
	private Long sourceId;
	@NotNull
	private LocalDateTime expirationDate;
}
