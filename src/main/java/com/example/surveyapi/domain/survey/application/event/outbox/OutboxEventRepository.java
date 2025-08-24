package com.example.surveyapi.domain.survey.application.event.outbox;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.survey.domain.dlq.OutboxEvent;

public interface OutboxEventRepository {

	void save(OutboxEvent event);

	void deleteAll(List<OutboxEvent> events);

	List<OutboxEvent> findPendingEvents();

	List<OutboxEvent> findPublishedEventsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}