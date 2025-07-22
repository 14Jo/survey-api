package com.example.surveyapi.domain.share.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.entity.Share;
import com.example.surveyapi.domain.share.domain.service.ShareDomainService;
import com.example.surveyapi.domain.share.domain.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.repository.ShareRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShareService {
	private final ShareRepository shareRepository;
	private final ShareDomainService shareDomainService;

	public ShareResponse createShare(Long surveyId) {
		Share share = shareDomainService.createShare(surveyId, ShareMethod.URL);
		Share saved = shareRepository.save(share);

		//event 발행 부분

		return ShareResponse.from(saved);
	}
}
