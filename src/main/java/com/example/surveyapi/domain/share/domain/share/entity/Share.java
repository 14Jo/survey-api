package com.example.surveyapi.domain.share.domain.share.entity;

import java.util.ArrayList;
import java.util.List;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;
import com.example.surveyapi.domain.share.domain.share.vo.ShareMethod;
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
	@Column(name = "survey_id", nullable = false)
	private Long surveyId;
	@Column(name = "creator_id", nullable = false)
	private Long creatorId;
	@Enumerated(EnumType.STRING)
	@Column(name = "method", nullable = false)
	private ShareMethod shareMethod;
	@Column(name = "link", nullable = false, unique = true)
	private String link;

	@OneToMany(mappedBy = "share", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Notification> notifications = new ArrayList<>();

	public Share(Long surveyId, Long creatorId, ShareMethod shareMethod, String linkUrl, List<Long> recipientIds) {
		this.surveyId = surveyId;
		this.creatorId = creatorId;
		this.shareMethod = shareMethod;
		this.link = linkUrl;

		createNotifications(recipientIds);
	}

	public boolean isAlreadyExist(String link) {
		boolean isExist = this.link.equals(link);
		return isExist;
	}

	public boolean isOwner(Long currentUserId) {
		if (!creatorId.equals(currentUserId)) {
			return true;
		}
		return false;
	}

	private void createNotifications(List<Long> recipientIds) {
		if(recipientIds == null || recipientIds.isEmpty()) {
			notifications.add(Notification.createForShare(this, this.creatorId));
			return;
		}
		recipientIds.forEach(recipientId -> {
			notifications.add(Notification.createForShare(this, recipientId));
		});
	}
}
