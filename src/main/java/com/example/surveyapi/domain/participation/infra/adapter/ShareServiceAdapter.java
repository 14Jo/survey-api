package com.example.surveyapi.domain.participation.infra.adapter;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.participation.application.client.ShareServicePort;
import com.example.surveyapi.global.client.ShareApiClient;

import lombok.RequiredArgsConstructor;

@Component("participationShareAdapter")
@RequiredArgsConstructor
public class ShareServiceAdapter implements ShareServicePort {

	private final ShareApiClient shareApiClient;
}
