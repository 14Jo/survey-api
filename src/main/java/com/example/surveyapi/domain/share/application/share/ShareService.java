package com.example.surveyapi.domain.share.application.share;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.client.ShareValidationResponse;
import com.example.surveyapi.domain.share.application.notification.NotificationService;
import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.repository.query.ShareQueryRepository;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
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
	private final NotificationService notificationService;

	public ShareResponse createShare(ShareSourceType sourceType, Long sourceId,
		Long creatorId, ShareMethod shareMethod,
		LocalDateTime expirationDate, List<Long> recipientIds,
		LocalDateTime notifyAt) {
		//TODO : 설문 존재 여부 검증

		Share share = shareDomainService.createShare(
			sourceType, sourceId,
			creatorId, shareMethod,
			expirationDate, recipientIds, notifyAt);
		Share saved = shareRepository.save(share);

		notificationService.create(saved, creatorId, notifyAt);

		return ShareResponse.from(saved);
	}

	@Transactional(readOnly = true)
	public ShareResponse getShare(Long shareId, Long currentUserId) {
		Share share = shareRepository.findById(shareId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		if (!share.isOwner(currentUserId)) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}

		return ShareResponse.from(share);
	}

	@Transactional(readOnly = true)
	public List<Share> getShareBySource(Long sourceId) {
		List<Share> shares = shareRepository.findBySource(sourceId);

		if (shares.isEmpty()) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}

		return shares;
	}

	@Transactional(readOnly = true)
	public ShareValidationResponse isRecipient(Long surveyId, Long userId) {
		boolean valid = shareQueryRepository.isExist(surveyId, userId);
		return new ShareValidationResponse(valid);
	}

	public String delete(Long shareId, Long currentUserId) {
		Share share = shareRepository.findById(shareId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		if (!share.isOwner(currentUserId)) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}
		shareRepository.delete(share);

		return "공유 삭제 완료";
	}

	@Transactional(readOnly = true)
	public Share getShareByToken(String token) {
		Share share = shareRepository.findByToken(token)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		if(share.isDeleted() || share.getExpirationDate().isBefore(LocalDateTime.now())) {
			throw new CustomException(CustomErrorCode.SHARE_EXPIRED);
		}

		return share;
	}

	public String getRedirectUrl(Share share) {
		return shareDomainService.getRedirectUrl(share);
	}
}
