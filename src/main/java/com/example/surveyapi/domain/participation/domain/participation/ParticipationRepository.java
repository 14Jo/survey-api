package com.example.surveyapi.domain.participation.domain.participation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParticipationRepository {
	Participation save(Participation participation);

	Page<Participation> findAll(Long memberId, Pageable pageable);
}
