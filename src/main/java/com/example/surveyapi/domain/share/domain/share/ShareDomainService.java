package com.example.surveyapi.domain.share.domain.share;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@Service
public class ShareDomainService {
	private static final String BASE_URL = "https://everysurvey.com/surveys/share/";
	private static final String BASE_EMAIL = "email://";

	public Share createShare(Long surveyId, Long creatorId, ShareMethod shareMethod) {
		String link = generateLink(shareMethod);
		return new Share(surveyId, creatorId, shareMethod, link);
	}

	public String generateLink(ShareMethod shareMethod) {
		String token = UUID.randomUUID().toString().replace("-", "");

		if(shareMethod == ShareMethod.URL) {
			return BASE_URL + token;
		} else if(shareMethod == ShareMethod.EMAIL) {
			return BASE_EMAIL + token;
		}
		throw new CustomException(CustomErrorCode.UNSUPPORTED_SHARE_METHOD);
	}
}
