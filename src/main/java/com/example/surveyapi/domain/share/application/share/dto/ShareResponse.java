package com.example.surveyapi.domain.share.application.share.dto;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;

import lombok.Getter;

@Getter
public class ShareResponse {
	private final Long id;
	private final Long surveyId;
	private final Long creatorId;
	private final ShareMethod shareMethod;
	private final String shareLink;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private ShareResponse(Share share) {
		this.id = share.getId();
		this.surveyId = share.getSurveyId();
		this.creatorId = share.getCreatorId();
		this.shareMethod = share.getShareMethod();
		this.shareLink = share.getLink();
		this.createdAt = share.getCreatedAt();
		this.updatedAt = share.getUpdatedAt();
	}

	public static ShareResponse from(Share share) {
		return new ShareResponse(share);
	}
}
