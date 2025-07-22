package com.example.surveyapi.domain.share.domain.notification.entity;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.domain.vo.Status;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "notifications")
public class Notification extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Column(name = "share_id", nullable = false)
	private Long shareId;
	@Column(name = "recipient_id", nullable = false)
	private Long recipientId;
	@Enumerated
	@Column(name = "status", nullable = false)
	private Status status;
	@Column(name = "sent_at")
	private LocalDateTime sentAt;
	@Column(name = "failed_reason")
	private String failedReason;

	public Notification(
		Long shareId,
		Long recipientId,
		Status status,
		LocalDateTime sentAt,
		String failedReason
	) {
		this.shareId = shareId;
		this.recipientId = recipientId;
		this.status = status;
		this.sentAt = sentAt;
		this.failedReason = failedReason;
	}
}
