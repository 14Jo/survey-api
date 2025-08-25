package com.example.surveyapi.survey.infra.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.survey.application.event.outbox.OutboxEventRepository;
import com.example.surveyapi.survey.domain.dlq.OutboxEvent;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxEventRepository {

	private final OutBoxJpaRepository jpaRepository;

	@Override
	public void save(OutboxEvent event) {
		jpaRepository.save(event);
	}

	@Override
	public void deleteAll(List<OutboxEvent> events) {
		jpaRepository.deleteAll(events);
	}

	@Override
	public List<OutboxEvent> findPendingEvents() {
		return jpaRepository.findPendingEvents();
	}

	@Override
	public List<OutboxEvent> findPublishedEventsOlderThan(LocalDateTime cutoffDate) {
		return jpaRepository.findPublishedEventsOlderThan(cutoffDate);
	}
}
