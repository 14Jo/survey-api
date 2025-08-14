package com.example.surveyapi.domain.share.application.event.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShareCreateRequest {
	private Long sourceId;
	private Long creatorId;
	private LocalDateTime expirationDate;
}
