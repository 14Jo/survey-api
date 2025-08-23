package com.example.surveyapi.domain.survey.application.event;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.survey.SurveyEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryablePublisher {

	private final RabbitTemplate rabbitTemplate;
	private final SurveyFallbackService fallbackService;

	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2.0)
	)
	public void publishDelayed(SurveyEvent event, String routingKey, long delayMs) {
		try {
			log.info("지연 이벤트 발행: routingKey={}, delayMs={}", routingKey, delayMs);
			Map<String, Object> headers = new HashMap<>();
			headers.put("x-delay", delayMs);
			rabbitTemplate.convertAndSend(RabbitConst.DELAYED_EXCHANGE_NAME, routingKey, event, message -> {
				message.getMessageProperties().getHeaders().putAll(headers);
				return message;
			});
			log.info("지연 이벤트 발행 성공: routingKey={}", routingKey);
		} catch (Exception e) {
			log.error("지연 이벤트 발행 실패: routingKey={}, error={}", routingKey, e.getMessage());
			throw e;
		}
	}

	@Recover
	public void recoverPublishDelayed(Exception ex, SurveyEvent event, String routingKey, long delayMs) {
		log.error("지연 이벤트 발행 최종 실패 - 풀백 실행: routingKey={}, error={}", routingKey, ex.getMessage());

	}
}
