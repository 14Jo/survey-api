package com.example.surveyapi.domain.share.infra.notification.sender;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotificationFactory {
	private final Map<String, NotificationSender> senderMap;

	public NotificationSender getSender(ShareMethod method) {
		NotificationSender sender = senderMap.get(method.name());
		if (sender == null) {
			throw new CustomException(CustomErrorCode.UNSUPPORTED_SHARE_METHOD);
		}

		return sender;
	}
}
