package com.example.surveyapi.domain.share.application.share;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.share.dto.ShareResponse;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.ShareDomainService;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ShareService {
	private final ShareRepository shareRepository;
	private final ShareDomainService shareDomainService;

	public ShareResponse createShare(ShareSourceType sourceType, Long sourceId,
		Long creatorId, LocalDateTime expirationDate) {
		Share share = shareDomainService.createShare(
			sourceType, sourceId,
			creatorId, expirationDate);
		Share saved = shareRepository.save(share);

		return ShareResponse.from(saved);
	}

	public void createNotifications(Long shareId, Long creatorId,
		ShareMethod shareMethod, List<String> emails,
		LocalDateTime notifyAt) {
		Share share = shareRepository.findById(shareId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		if (!share.isOwner(creatorId)) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED_SHARE);
		}

		share.createNotifications(shareMethod, emails, notifyAt);
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
	public Share getShareEntity(Long shareId, Long currentUserId) {
		Share share = shareRepository.findById(shareId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		if (!share.isOwner(currentUserId)) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}

		return share;
	}

	@Transactional(readOnly = true)
	public Share getShareBySource(ShareSourceType sourceType, Long sourceId) {
		Share share = shareRepository.findBySource(sourceType, sourceId);

		if (share.isDeleted()) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}

		return share;
	}

	@Transactional(readOnly = true)
	public List<Share> getShareBySourceId(Long sourceId) {
		List<Share> shares = shareRepository.findBySourceId(sourceId);

		if (shares.isEmpty()) {
			throw new CustomException(CustomErrorCode.NOT_FOUND_SHARE);
		}

		return shares;
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

	public String getRedirectUrl(String token, ShareSourceType sourceType) {
		Share share = getShareByToken(token);

		if (share.getSourceType() != sourceType) {
			throw new CustomException(CustomErrorCode.INVALID_SHARE_TYPE);
		}

		return shareDomainService.getRedirectUrl(share);
	}
}
