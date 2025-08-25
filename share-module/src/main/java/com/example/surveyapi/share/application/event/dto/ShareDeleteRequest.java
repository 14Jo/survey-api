package com.example.surveyapi.share.application.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShareDeleteRequest {
	private Long projectId;
	private Long deleterId;
}
