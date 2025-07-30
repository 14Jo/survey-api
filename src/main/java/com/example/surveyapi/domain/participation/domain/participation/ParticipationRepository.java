package com.example.surveyapi.domain.participation.domain.participation;

import java.util.List;
import java.util.Optional;

import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationQueryRepository;

public interface ParticipationRepository extends ParticipationQueryRepository {
	Participation save(Participation participation);

	List<Participation> findAllBySurveyIdIn(List<Long> surveyIds);

	Optional<Participation> findById(Long participationId);
}
