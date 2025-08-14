package com.example.surveyapi.domain.share.application.share.dto;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

import lombok.Getter;

@Getter
public class ShareResponse {
	private final Long id;
	private final ShareSourceType sourceType;
	private final Long sourceId;
	private final Long creatorId;
	private final String token;
	private final String shareLink;
	private final LocalDateTime expirationDate;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	private ShareResponse(Share share) {
		this.id = share.getId();
		this.sourceType = share.getSourceType();
		this.sourceId = share.getSourceId();
		this.creatorId = share.getCreatorId();
		this.token = share.getToken();
		this.shareLink = share.getLink();
		this.expirationDate = share.getExpirationDate();
		this.createdAt = share.getCreatedAt();
		this.updatedAt = share.getUpdatedAt();
	}

	public static ShareResponse from(Share share) {
		return new ShareResponse(share);
	}
}
