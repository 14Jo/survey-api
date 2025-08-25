package com.example.surveyapi.share.domain.notification.entity;

import java.time.LocalDateTime;

import com.example.surveyapi.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.share.domain.notification.vo.Status;
import com.example.surveyapi.share.domain.share.entity.Share;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
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
	@Enumerated(EnumType.STRING)
	@Column(name = "share_method")
	private ShareMethod shareMethod;
	@Column(name = "recipient_id")
	private Long recipientId;
	@Column(name = "recipient_email")
	private String recipientEmail;
	@Enumerated(EnumType.STRING)
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
		ShareMethod shareMethod,
		Long recipientId,
		String recipientEmail,
		Status status,
		LocalDateTime sentAt,
		String failedReason,
		LocalDateTime notifyAt
	) {
		this.share = share;
		this.shareMethod = shareMethod;
		this.recipientId = recipientId;
		this.recipientEmail = recipientEmail;
		this.status = status;
		this.sentAt = sentAt;
		this.failedReason = failedReason;
		this.notifyAt = notifyAt;
	}

	public static Notification createForShare(Share share, ShareMethod shareMethod, Long recipientId, String recipientEmail, LocalDateTime notifyAt) {
		return new Notification(share, shareMethod, recipientId, recipientEmail, Status.READY_TO_SEND, null, null, notifyAt);
	}

	public void setSent() {
		this.status = Status.SENT;
		this.sentAt = LocalDateTime.now();
	}

	public void setFailed(String failedReason) {
		this.status = Status.FAILED;
		this.failedReason = failedReason;
	}

	public void setCheck() {
		this.status = Status.CHECK;
	}
}
