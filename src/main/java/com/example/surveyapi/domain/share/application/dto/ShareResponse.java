package com.example.surveyapi.domain.share.application.dto;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.domain.entity.Share;
import com.example.surveyapi.domain.share.domain.vo.ShareMethod;

import lombok.Getter;

@Getter
public class ShareResponse {
	private final Long id;
	private final Long surveyId;
	private final ShareMethod shareMethod;
	private final String shareLink;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private ShareResponse(
		Long id,
		Long surveyId,
		ShareMethod shareMethod,
		String shareLink,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
	) {
		this.id = id;
		this.surveyId = surveyId;
		this.shareMethod = shareMethod;
		this.shareLink = shareLink;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public static ShareResponse from(Share share) {
		ShareResponse result = new ShareResponse(
			share.getId(),
			share.getSurveyId(),
			share.getShareMethod(),
			share.getLink(),
			share.getCreatedAt(),
			share.getUpdatedAt()
		);

		return result;
	}
}
