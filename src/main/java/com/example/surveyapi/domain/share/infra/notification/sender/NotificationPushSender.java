package com.example.surveyapi.domain.share.infra.notification.sender;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.fcm.entity.FcmToken;
import com.example.surveyapi.domain.share.domain.fcm.repository.FcmTokenRepository;
import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("PUSH")
@RequiredArgsConstructor
public class NotificationPushSender implements NotificationSender {
	private final FcmTokenRepository tokenRepository;
	private final FirebaseMessaging firebaseMessaging;

	private static final Map<ShareSourceType, PushContent> pushContentMap;

	static {
		pushContentMap = new EnumMap<>(ShareSourceType.class);
		pushContentMap.put(ShareSourceType.PROJECT_MANAGER, new NotificationPushSender.PushContent(
			"회원님께서 프로젝트 관리자로 등록되었습니다.", "회원님께서 프로젝트 관리자로 등록되었습니다."));
		pushContentMap.put(ShareSourceType.PROJECT_MEMBER, new NotificationPushSender.PushContent(
			"회원님게서 프로젝트 대상자로 등록되었습니다.", "회원님께서 프로젝트 대상자로 등록되었습니다."));
		pushContentMap.put(ShareSourceType.SURVEY, new NotificationPushSender.PushContent(
			"회원님께서 설문 대상자로 등록되었습니다.", "회원님께서 설문 대상자로 등록되었습니다. 지금 설문에 참여해보세요!"));
	}

	private record PushContent(String title, String body) {}


	@Override
	public void send(Notification notification) {

		Long userId = notification.getRecipientId();
		Optional<FcmToken> fcmToken = tokenRepository.findByUserId(userId);

		if(fcmToken.isEmpty()) {
			log.info("userId: {} - 토큰이 존재하지 않습니다.", userId);
			return;
		}

		String token = fcmToken.get().getToken();

		ShareSourceType sourceType = notification.getShare().getSourceType();
		PushContent content = pushContentMap.getOrDefault(sourceType, null);

		if(content == null) {
			log.error("알 수 없는 ShareSourceType: {}", sourceType);
			return;
		}

		Message message = Message.builder()
			.setToken(token)
			.putData("title", content.title())
			.putData("body", content.body() + "\n" + notification.getShare().getLink())
			.build();

		try{
			String response = firebaseMessaging.send(message);
			log.info("userId: {}, notificationId: {}, response: {} - PUSH 알림 발송", userId, notification.getId(), response);
		} catch (FirebaseMessagingException e) {
			log.error("userId: {}, notificationId: {} - PUSH 전송 실패", userId, notification.getId());
			throw new CustomException(CustomErrorCode.PUSH_FAILED);
		}
	}
}
