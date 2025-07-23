package com.example.surveyapi.domain.share.application.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.share.application.notification.dto.NotificationPageResponse;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.repository.NotificationRepository;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.domain.share.domain.share.repository.ShareRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {
	private final NotificationRepository notificationRepository;
	private final ShareRepository shareRepository;

	public NotificationPageResponse gets(Long shareId, Long requesterId, int page, int size) {
		Share share = shareRepository.findById(shareId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SHARE));

		//TODO : 접근 권한 체크(User 테이블 참조?) - 요청자와 생성자가 일치하는지 + 관리자인지

		Pageable pageable = PageRequest.of(
			page,
			size,
			Sort.by(Sort.Direction.DESC,
				"sentAt"));
		Page<Notification> notifications = notificationRepository.findByShareId(shareId, pageable);
		return NotificationPageResponse.from(notifications);
	}

	private boolean isAdmin(Long userId) {
		//TODO : 관리자 권한 조회 기능, 접근 권한 확인 기능 구현 시 동시에 구현 및 사용
		return false;
	}
}
