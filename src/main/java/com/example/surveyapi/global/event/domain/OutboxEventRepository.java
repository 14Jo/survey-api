package com.example.surveyapi.global.event.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.survey.application.event.OutboxEventStatus;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

	@Query("SELECT o FROM OutboxEvent o WHERE o.status = 'PENDING' " +
		"AND (o.nextRetryAt IS NULL OR o.nextRetryAt <= :now) " +
		"AND (o.scheduledAt IS NULL OR o.scheduledAt <= :now) " +
		"ORDER BY o.createdAt ASC")
	List<OutboxEvent> findEventsToProcess(@Param("now") LocalDateTime now);

	@Query("SELECT o FROM OutboxEvent o WHERE o.status = :status " +
		"AND o.scheduledAt IS NOT NULL AND o.scheduledAt <= :now " +
		"ORDER BY o.scheduledAt ASC")
	List<OutboxEvent> findReadyDelayedEvents(@Param("status") OutboxEventStatus status,
		@Param("now") LocalDateTime now);

	List<OutboxEvent> findByStatusAndRetryCountLessThan(OutboxEventStatus status, int maxRetryCount);

	@Query("SELECT o FROM OutboxEvent o WHERE o.status = 'PUBLISHED' " +
		"AND o.publishedAt < :cutoffDate")
	List<OutboxEvent> findPublishedEventsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}