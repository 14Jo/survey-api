package com.example.surveyapi.domain.share.api.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.surveyapi.domain.share.application.client.ShareValidationResponse;
import com.example.surveyapi.domain.share.application.share.ShareService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/shares")
public class ShareInternalController {
	private final ShareService shareService;

	@GetMapping("/validation")
	public ShareValidationResponse validateUserRecipient(
		@RequestParam Long surveyId,
		@RequestParam Long userId
	) {
		return shareService.isRecipient(surveyId, userId);
	}
}
