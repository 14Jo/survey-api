package com.example.surveyapi.domain.share.infra.notification.sender;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;

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
		message.setSubject(subject(notification.getShare().getSourceType()));
		message.setText(notification.getShare().getLink());

		mailSender.send(message);
	}

	private String subject(ShareSourceType sourceType) {
		String result;

		if(sourceType == ShareSourceType.PROJECT_MANAGER) {
			result = "회원님께서 프로젝트 관리자로 등록되었습니다.";
		} else if(sourceType == ShareSourceType.PROJECT_MEMBER) {
			result = "회원님께서 프로젝트 대상자로 등록되었습니다.";
		} else {
			result = "회원님께서 설문 대상자로 등록되었습니다.";
		}

		return result;
	}
}
