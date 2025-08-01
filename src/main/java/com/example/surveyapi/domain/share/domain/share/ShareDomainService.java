package com.example.surveyapi.domain.share.domain.share;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@Service
public class ShareDomainService {
	private static final String SURVEY_URL = "https://localhost:8080/api/v2/share/surveys/";
	private static final String PROJECT_URL = "https://localhost:8080/api/v2/share/projects/";

	public Share createShare(ShareSourceType sourceType, Long sourceId,
		Long creatorId, LocalDateTime expirationDate,
		List<Long> recipientIds) {
		String token = UUID.randomUUID().toString().replace("-", "");
		String link = generateLink(sourceType, token);

		return new Share(sourceType, sourceId, creatorId, token, link, expirationDate, recipientIds);
	}

	public String generateLink(ShareSourceType sourceType, String token) {

		if(sourceType == ShareSourceType.SURVEY) {
			return SURVEY_URL + token;
		} else if(sourceType == ShareSourceType.PROJECT) {
			return PROJECT_URL + token;
		}
		throw new CustomException(CustomErrorCode.UNSUPPORTED_SHARE_METHOD);
	}
}
