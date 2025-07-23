package com.example.surveyapi.domain.share.domain.share;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;

@Service
public class ShareDomainService {
	private static final String BASE_URL = "https://everysurvey.com/surveys/share/";

	public Share createShare(Long surveyId, ShareMethod shareMethod) {
		String link = generateLink();
		return new Share(surveyId, shareMethod, link);
	}

	public String generateLink() {
		String token = UUID.randomUUID().toString().replace("-", "");
		return BASE_URL + token;
	}
}
