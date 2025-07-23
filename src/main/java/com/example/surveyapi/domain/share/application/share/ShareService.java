package com.example.surveyapi.domain.share.application.share;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.event.ShareCreateEvent;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShareService {
	private final ShareRepository shareRepository;
	private final ShareDomainService shareDomainService;
	private final ApplicationEventPublisher eventPublisher;

	public ShareResponse createShare(Long surveyId) {
		Share share = shareDomainService.createShare(surveyId, ShareMethod.URL);
		Share saved = shareRepository.save(share);

		eventPublisher.publishEvent(new ShareCreateEvent(saved.getId(), saved.getSurveyId()));

		return ShareResponse.from(saved);
	}
}
