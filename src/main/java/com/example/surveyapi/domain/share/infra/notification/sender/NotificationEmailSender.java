package com.example.surveyapi.domain.share.infra.notification.sender;

import java.util.EnumMap;
import java.util.Map;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EMAIL")
@RequiredArgsConstructor
public class NotificationEmailSender implements NotificationSender {
	private final JavaMailSender mailSender;

	private static final Map<ShareSourceType, EmailContent> emailContentMap;

	static {
		emailContentMap = new EnumMap<>(ShareSourceType.class);
		emailContentMap.put(ShareSourceType.PROJECT_MANAGER, new EmailContent(
			"회원님께서 프로젝트 관리자로 등록되었습니다.", "Link : "));
		emailContentMap.put(ShareSourceType.PROJECT_MEMBER, new EmailContent(
			"회원님게서 프로젝트 대상자로 등록되었습니다.", "Link : "));
		emailContentMap.put(ShareSourceType.SURVEY, new EmailContent(
			"회원님께서 설문 대상자로 등록되었습니다.", "지금 설문에 참여해보세요!\nLink : "));
	}

	private record EmailContent(String subject, String text) {}

	@Override
	public void send(Notification notification) {
		log.info("이메일 전송: {}", notification.getId());
		ShareSourceType sourceType = notification.getShare().getSourceType();
		EmailContent content = emailContentMap.getOrDefault(sourceType, null);

		if(content == null) {
			log.error("알 수 없는 ShareSourceType: {}", sourceType);
			return;
		}

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(notification.getRecipientEmail());
		message.setSubject(content.subject());
		message.setText(content.text() + notification.getShare().getLink());

		mailSender.send(message);
	}
}
