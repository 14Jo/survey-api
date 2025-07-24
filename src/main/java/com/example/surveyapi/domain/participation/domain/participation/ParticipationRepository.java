package com.example.surveyapi.domain.participation.domain.participation;

import java.util.List;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParticipationRepository {
	Participation save(Participation participation);

	Page<Participation> findAll(Long memberId, Pageable pageable);

	List<Participation> findAllBySurveyIdIn(List<Long> surveyIds);

	Optional<Participation> findById(Long participationId);
}
