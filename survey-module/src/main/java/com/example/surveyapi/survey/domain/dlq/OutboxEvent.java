package com.example.surveyapi.survey.domain.dlq;

import java.time.LocalDateTime;

import com.example.surveyapi.survey.application.event.enums.OutboxEventStatus;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "outbox_event")
@Getter
@NoArgsConstructor
public class OutboxEvent extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "outbox_event_id")
	private Long outboxEventId;

	@Column(name = "aggregate_type", nullable = false)
	private String aggregateType;

	@Column(name = "aggregate_id", nullable = false)
	private Long aggregateId;

	@Column(name = "event_type", nullable = false)
	private String eventType;

	@Column(name = "event_data", columnDefinition = "TEXT")
	private String eventData;

	@Column(name = "routing_key")
	private String routingKey;

	@Column(name = "exchange_name")
	private String exchangeName;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false)
	private OutboxEventStatus status = OutboxEventStatus.PENDING;

	@Column(name = "retry_count")
	private int retryCount = 0;

	@Column(name = "max_retry_count")
	private int maxRetryCount = 3;

	@Column(name = "next_retry_at")
	private LocalDateTime nextRetryAt;

	@Column(name = "error_message", columnDefinition = "TEXT")
	private String errorMessage;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	@Column(name = "scheduled_at")
	private LocalDateTime scheduledAt;

	@Column(name = "delay_ms")
	private Long delayMs;

	public static OutboxEvent create(
		String aggregateType,
		Long aggregateId,
		String eventType,
		String eventData,
		String routingKey,
		String exchangeName
	) {
		OutboxEvent outboxEvent = new OutboxEvent();
		outboxEvent.aggregateType = aggregateType;
		outboxEvent.aggregateId = aggregateId;
		outboxEvent.eventType = eventType;
		outboxEvent.eventData = eventData;
		outboxEvent.routingKey = routingKey;
		outboxEvent.exchangeName = exchangeName;
		return outboxEvent;
	}

	public static OutboxEvent createDelayed(
		String aggregateType,
		Long aggregateId,
		String eventType,
		String eventData,
		String routingKey,
		String exchangeName,
		long delayMs,
		LocalDateTime scheduledAt
	) {
		OutboxEvent outboxEvent = create(aggregateType, aggregateId, eventType, eventData, routingKey, exchangeName);
		outboxEvent.delayMs = delayMs;
		outboxEvent.scheduledAt = scheduledAt;
		return outboxEvent;
	}

	public void asPublish() {
		this.status = OutboxEventStatus.PUBLISHED;
		this.publishedAt = LocalDateTime.now();
	}

	public void asFailed(String errorMessage) {
		this.status = OutboxEventStatus.FAILED;
		this.errorMessage = errorMessage;
		this.retryCount++;

		if (this.retryCount < this.maxRetryCount) {
			this.status = OutboxEventStatus.PENDING;
			this.nextRetryAt = LocalDateTime.now().plusMinutes((long)Math.pow(2, this.retryCount));
		}
	}

	public boolean isDelayedEvent() {
		return this.delayMs != null && this.scheduledAt != null;
	}

	public boolean isReadyForDelivery() {
		if (this.scheduledAt != null) {
			return LocalDateTime.now().isAfter(this.scheduledAt);
		}
		return true;
	}
}