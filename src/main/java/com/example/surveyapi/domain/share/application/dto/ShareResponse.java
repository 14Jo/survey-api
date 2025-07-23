package com.example.surveyapi.domain.share.application.dto;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
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

	public ShareResponse(Share share) {
		this.id = share.getId();
		this.surveyId = share.getSurveyId();
		this.shareMethod = share.getShareMethod();
		this.shareLink = share.getLink();
		this.createdAt = share.getCreatedAt();
		this.updatedAt = share.getUpdatedAt();
	}
}
