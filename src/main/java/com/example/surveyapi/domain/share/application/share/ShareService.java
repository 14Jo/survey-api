package com.example.surveyapi.domain.share.application.share;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.client.ShareValidationResponse;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.repository.query.ShareQueryRepository;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShareService {
	private final ShareRepository shareRepository;
	private final ShareQueryRepository shareQueryRepository;
	private final ShareDomainService shareDomainService;

	public ShareResponse createShare(Long surveyId, Long creatorId, ShareMethod shareMethod, List<Long> recipientIds) {
		//TODO : 설문 존재 여부 검증

		Share share = shareDomainService.createShare(surveyId, creatorId, shareMethod, recipientIds);
		Share saved = shareRepository.save(share);

		return ShareResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public ShareResponse getShare(Long shareId, Long currentUserId) {
		Share share = shareRepository.findById(shareId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		// TODO : 권한 검증 - 관리자(admin)의 경우 추후 추가 예정

		if (share.isOwner(currentUserId)) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}

		return ShareResponse.from(share);
	}

	@Transactional(readOnly = true)
	public ShareValidationResponse isRecipient(Long surveyId, Long userId) {
		boolean valid = shareQueryRepository.isExist(surveyId, userId);
		return new ShareValidationResponse(valid);
	}
}
