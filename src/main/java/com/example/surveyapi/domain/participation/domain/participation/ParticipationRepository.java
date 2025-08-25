package com.example.surveyapi.domain.participation.domain.participation;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationProjection;

public interface ParticipationRepository {
	Participation save(Participation participation);

	List<Participation> findAllBySurveyIdIn(List<Long> surveyIds);

	Optional<Participation> findById(Long participationId);

	boolean exists(Long surveyId, Long userId);

	Page<ParticipationInfo> findParticipationInfos(Long userId, Pageable pageable);

	Map<Long, Long> countsBySurveyIds(List<Long> surveyIds);

	List<ParticipationProjection> findParticipationProjectionsBySurveyIds(List<Long> surveyIds);

	Optional<ParticipationProjection> findParticipationProjectionByIdAndUserId(Long participationId, Long loginUserId);
}
