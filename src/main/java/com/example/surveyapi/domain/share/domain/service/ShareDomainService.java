package com.example.surveyapi.domain.share.domain.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.share.domain.entity.Share;
import com.example.surveyapi.domain.share.domain.vo.ShareMethod;

@Service
public class ShareDomainService {
	private static final String BASE_URL = "https://everysurvey.com/surveys/share/";

	public Share createShare(Long surveyId, ShareMethod shareMethod) {
		String link = generateLink(surveyId);
		return new Share(surveyId, shareMethod, link);
	}

	public String generateLink(Long surveyId) {
		String token = UUID.randomUUID().toString().replace("-", "");
		return BASE_URL + token;
	}
}
