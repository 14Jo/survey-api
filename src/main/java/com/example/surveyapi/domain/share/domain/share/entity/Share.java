package com.example.surveyapi.domain.share.domain.share.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.notification.vo.ShareMethod;
import com.example.surveyapi.domain.share.domain.share.vo.ShareSourceType;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "share")
public class Share extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	@Enumerated(EnumType.STRING)
	@Column(name = "source_type", nullable = false)
	private ShareSourceType sourceType;
	@Column(name = "source_id", nullable = false)
	private Long sourceId;
	@Column(name = "creator_id", nullable = false)
	private Long creatorId;
	@Column(name = "token", nullable = false)
	private String token;
	@Column(name = "link", nullable = false, unique = true)
	private String link;
	@Column(name = "expiration", nullable = false)
	private LocalDateTime expirationDate;

	@OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notification> notifications = new ArrayList<>();

	public Share(ShareSourceType sourceType, Long sourceId,
		Long creatorId,	String token,
		String link, LocalDateTime expirationDate) {
		this.sourceType = sourceType;
		this.sourceId = sourceId;
		this.creatorId = creatorId;
		this.token = token;
		this.link = link;
		this.expirationDate = expirationDate;

	}

	public boolean isAlreadyExist(String link) {
		boolean isExist = this.link.equals(link);
		return isExist;
	}

	public boolean isOwner(Long currentUserId) {
		if (creatorId.equals(currentUserId)) {
			return true;
		}
		return false;
	}

	public void createNotifications(ShareMethod shareMethod, List<String> emails, LocalDateTime notifyAt) {
		if(shareMethod == ShareMethod.URL) {
			return;
		}

		if(emails == null || emails.isEmpty()) {
			notifications.add(
				Notification.createForShare(
					this, shareMethod,
					this.creatorId, null,
					notifyAt)
			);

			return;
		}
		emails.forEach(email -> {
			notifications.add(
				Notification.createForShare(
					this, shareMethod,
					null, email,
					notifyAt)
			);
		});
	}

	public boolean isDeleted() {
		return isDeleted;
	}
}
