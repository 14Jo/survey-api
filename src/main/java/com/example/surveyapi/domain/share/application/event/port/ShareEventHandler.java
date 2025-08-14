package com.example.surveyapi.domain.share.application.event.port;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.application.event.dto.ShareCreateRequest;
import com.example.surveyapi.domain.share.application.share.ShareService;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ShareEventHandler implements ShareEventPort {
	private final ShareService shareService;

	@Override
	public void handleSurveyEvent(ShareCreateRequest request) {
		shareService.createShare(
			ShareSourceType.SURVEY,
			request.getSourceId(),
			request.getCreatorId(),
			request.getExpirationDate()
		);
	}
}
