package com.example.surveyapi.domain.share.domain.notification.entity;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.share.domain.notification.vo.Status;
import com.example.surveyapi.domain.share.domain.share.entity.Share;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "share_id")
	private Share share;
	@Column(name = "recipient_id", nullable = false)
	private Long recipientId;
	@Enumerated
	@Column(name = "status", nullable = false)
	private Status status;
	@Column(name = "sent_at")
	private LocalDateTime sentAt;
	@Column(name = "failed_reason")
	private String failedReason;
	@Column(name = "notify_at")
	private LocalDateTime notifyAt;

	public Notification(
		Share share,
		Long recipientId,
		Status status,
		LocalDateTime sentAt,
		String failedReason,
		LocalDateTime notifyAt
	) {
		this.share = share;
		this.recipientId = recipientId;
		this.status = status;
		this.sentAt = sentAt;
		this.failedReason = failedReason;
		this.notifyAt = notifyAt;
	}

	public static Notification createForShare(Share share, Long recipientId, LocalDateTime notifyAt) {
		return new Notification(share, recipientId, Status.READY_TO_SEND, null, null, notifyAt);
	}

	public void setSent() {
		this.status = Status.SENT;
		this.sentAt = LocalDateTime.now();
	}

	public void setFailed(String failedReason) {
		this.status = Status.FAILED;
		this.failedReason = failedReason;
	}
}
