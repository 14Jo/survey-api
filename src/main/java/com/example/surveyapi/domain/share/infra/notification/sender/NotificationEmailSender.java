package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.infra.annotation.ShareEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("EMAIL")
@RequiredArgsConstructor
public class NotificationEmailSender implements NotificationSender {
	private final JavaMailSender mailSender;

	@Override
	public void send(Notification notification) {
		log.info("이메일 전송: {}", notification.getId());

		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(notification.getRecipientEmail());
		message.setSubject("공유 알림");
		message.setText(notification.getShare().getLink());

		mailSender.send(message);
	}
}
