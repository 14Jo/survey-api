package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.survey.application.event.enums.OutboxEventStatus;
import com.example.surveyapi.domain.survey.domain.dlq.OutboxEvent;

public interface OutboxEventRepository {

	void save(OutboxEvent event);

	void deleteAll(List<OutboxEvent> events)

	List<OutboxEvent> findEventsToProcess(@Param("now") LocalDateTime now);

	List<OutboxEvent> findPublishedEventsOlderThan(@Param("cutoffDate") LocalDateTime cutoffDate);
}