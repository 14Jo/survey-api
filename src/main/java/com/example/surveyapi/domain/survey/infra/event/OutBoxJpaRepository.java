package com.example.surveyapi.domain.survey.infra.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.survey.domain.dlq.OutboxEvent;

public interface OutBoxJpaRepository extends JpaRepository<OutboxEvent, Integer> {

	@Query("SELECT o FROM OutboxEvent o WHERE o.status = 'PENDING' " +
		"AND (o.nextRetryAt IS NULL OR o.nextRetryAt <= :now) " +
		"ORDER BY o.createdAt ASC")
	List<OutboxEvent> findEventsToProcess(@Param("now") LocalDateTime now);

	@Query("SELECT o FROM OutboxEvent o WHERE o.status = 'PENDING' " +
		"ORDER BY o.createdAt ASC")
	List<OutboxEvent> findPendingEvents();

	@Query("SELECT o FROM OutboxEvent o WHERE o.status = 'PUBLISHED' " +
		"AND o.publishedAt < :cutoffDate")
	List<OutboxEvent> findPublishedEventsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}
