package com.example.surveyapi.domain.share.application.share.dto;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

import lombok.Getter;

@Getter
public class ShareResponse {
	private final String shareLink;
	private final LocalDateTime expirationDate;
	private final LocalDateTime createdAt;

	private ShareResponse(Share share) {
		this.shareLink = share.getLink();
		this.expirationDate = share.getExpirationDate();
		this.createdAt = share.getCreatedAt();
	}

	public static ShareResponse from(Share share) {
		return new ShareResponse(share);
	}
}
