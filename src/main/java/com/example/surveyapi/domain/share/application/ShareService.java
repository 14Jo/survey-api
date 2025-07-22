package com.example.surveyapi.domain.share.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.domain.ShareLinkGenerator;
import com.example.surveyapi.domain.share.domain.entity.Share;
import com.example.surveyapi.domain.share.domain.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.repository.ShareRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShareService {
	private final ShareRepository shareRepository;
	private final ShareLinkGenerator shareLinkGenerator;

	public Share createShare(Long surveyId) {
		String link = shareLinkGenerator.generateLink(surveyId);

		Share share = new Share(surveyId, ShareMethod.URL, link);
		Share saved = shareRepository.save(share);

		//event 발행 부분

		return saved;
	}
}
