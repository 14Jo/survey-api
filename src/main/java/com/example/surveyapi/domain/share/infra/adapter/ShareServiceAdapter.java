package com.example.surveyapi.domain.share.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.client.ShareServicePort;
import com.example.surveyapi.global.config.client.share.ShareApiClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShareServiceAdapter implements ShareServicePort {
	private final ObjectMapper objectMapper;
	private final ShareApiClient shareApiClient;

	@Override
	public List<Long> getRecipientIds(Long shareId, Long recipientId) {
		List<Long> recipientIds = List.of(2L, 3L, 4L);
		return recipientIds;
	}
}
