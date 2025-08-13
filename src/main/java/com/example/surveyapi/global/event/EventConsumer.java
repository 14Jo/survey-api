package com.example.surveyapi.global.event;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.model.ParticipationEvent;
import com.example.surveyapi.global.model.SurveyEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventConsumer {

	private final ObjectMapper objectMapper;

	//SurveyEvent를 배치로 처리
	@RabbitListener(
		queues = RabbitConst.QUEUE_NAME,
		containerFactory = "batchListenerContainerFactory"
	)
	public void handleSurveyEventBatch(List<Message> messages, Channel channel) {
		log.info("설문 이벤트 배치 처리 시작: {}개 메시지", messages.size());

		try {
			// 메시지를 SurveyEvent로 변환
			List<SurveyEvent> events = messages.stream()
				.map(this::convertToSurveyEvent)
				.collect(Collectors.toList());

			// 이벤트 타입별로 배치 처리
			processSurveyEventBatch(events);

			// 성공 시 모든 메시지 확인
			acknowledgeAllMessages(messages, channel);

			log.info("설문 이벤트 배치 처리 완료: {}개 메시지", messages.size());

		} catch (Exception e) {
			log.error("설문 이벤트 배치 처리 실패: {}개 메시지, 에러: {}",
				messages.size(), e.getMessage());

			// 실패 시 모든 메시지 거부 (재시도)
			rejectAllMessages(messages, channel);
		}
	}

	// 이벤트 타입별로 배치 처리
	private void processSurveyEventBatch(List<SurveyEvent> events) {
		log.info("이벤트 타입별 배치 처리 시작: {}개 이벤트", events.size());

		// Activate 이벤트 처리
		List<SurveyActivateEvent> activateEvents = events.stream()
			.filter(event -> event instanceof SurveyActivateEvent)
			.map(event -> (SurveyActivateEvent)event)
			.collect(Collectors.toList());

		if (!activateEvents.isEmpty()) {
			processSurveyActivateBatch(activateEvents);
		}
	}

	// 설문 활성화/비활성화 이벤트 처리
	private void processSurveyActivateBatch(List<SurveyActivateEvent> events) {
		log.info("설문 활성화 배치 처리 시작: {}개 설문", events.size());

		try {
			//TODO 이벤트들 처리 기능 호출 (Share 도메인 호출 등)

			log.info("알림 발송 배치 완료: {}개 설문", events.size());

		} catch (Exception e) {
			log.error("설문 활성화 배치 처리 실패: {}개 설문, 에러: {}",
				events.size(), e.getMessage());
			throw e;
		}
	}

	// 메시지를 SurveyEvent로 변환
	private SurveyEvent convertToSurveyEvent(Message message) {
		try {
			String json = new String(message.getBody());

			// JSON에서 @type 필드를 확인하여 적절한 클래스로 변환
			if (json.contains("SurveyActivateEvent")) {
				return objectMapper.readValue(json, SurveyActivateEvent.class);
			} else {
				log.warn("알 수 없는 이벤트 타입: {}", json);
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException("SurveyEvent 변환 실패", e);
		}
	}

	private ParticipationEvent convertToParticipationEvent(Message message) {
		try {
			String json = new String(message.getBody());

			if (json.contains("ParticipationCreatedEvent")) {
				return objectMapper.readValue(json, ParticipationCreatedEvent.class);
			} else if (json.contains("ParticipationUpdatedEvent")) {
				return objectMapper.readValue(json, ParticipationUpdatedEvent.class);
			} else {
				log.warn("알 수 없는 이벤트 타입: {}", json);
				return null;
			}
		} catch (Exception e) {
			throw new RuntimeException("ParticipationEvent 변환 실패", e);
		}
	}

	//성공 시 모든 메시지 확인
	private void acknowledgeAllMessages(List<Message> messages, Channel channel) {
		for (Message message : messages) {
			try {
				channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
			} catch (IOException e) {
				log.error("메시지 확인 실패: {}", e.getMessage());
			}
		}
	}

	// 실패 시 모든 메시지 거부 (재시도)
	private void rejectAllMessages(List<Message> messages, Channel channel) {
		for (Message message : messages) {
			try {
				channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
			} catch (IOException e) {
				log.error("메시지 거부 실패: {}", e.getMessage());
			}
		}
	}
}
