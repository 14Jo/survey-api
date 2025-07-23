package com.example.surveyapi.domain.share.infra.notification.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.share.domain.notification.entity.Notification;

public interface NotificationJpaRepository extends JpaRepository<Notification, Long> {
}
