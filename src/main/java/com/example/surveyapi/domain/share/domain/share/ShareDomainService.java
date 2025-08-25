package com.example.surveyapi.domain.share.domain.share;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@Service
public class ShareDomainService {
	private static final String SURVEY_URL = "https://localhost:8080/share/surveys/";
	private static final String PROJECT_MEMBER_URL = "https://localhost:8080/share/projects/members/";
	private static final String PROJECT_MANAGER_URL = "https://localhost:8080/share/projects/managers/";

	public Share createShare(ShareSourceType sourceType, Long sourceId,
		Long creatorId,	LocalDateTime expirationDate) {
		String token = UUID.randomUUID().toString().replace("-", "");
		String link = generateLink(sourceType, token);

		return new Share(sourceType, sourceId,
			creatorId, token,
			link, expirationDate);
	}

	public String generateLink(ShareSourceType sourceType, String token) {

		if(sourceType == ShareSourceType.SURVEY) {
			return SURVEY_URL + token;
		} else if(sourceType == ShareSourceType.PROJECT_MEMBER) {
			return PROJECT_MEMBER_URL + token;
		} else if(sourceType == ShareSourceType.PROJECT_MANAGER) {
			return PROJECT_MANAGER_URL + token;
		}
		throw new CustomException(CustomErrorCode.UNSUPPORTED_SHARE_METHOD);
	}

	public String getRedirectUrl(Share share) {
		if (share.getSourceType() == ShareSourceType.PROJECT_MEMBER) {
			return "https://localhost:8080/api/projects/" + share.getSourceId() + "/members";
		} else if(share.getSourceType() == ShareSourceType.PROJECT_MANAGER) {
			return "https://localhost:8080/api/projects/" + share.getSourceId() + "/managers";
		} else if (share.getSourceType() == ShareSourceType.SURVEY) {
			return "https://localhost:8080/surveys/" + share.getSourceId();
		}
		throw new CustomException(CustomErrorCode.INVALID_SHARE_TYPE);
	}
}
