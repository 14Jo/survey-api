package com.example.surveyapi.domain.participation.infra;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.domain.participation.domain.participation.query.ParticipationProjection;
import com.example.surveyapi.domain.participation.infra.dsl.ParticipationQueryDslRepository;
import com.example.surveyapi.domain.participation.infra.jpa.JpaParticipationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ParticipationRepositoryImpl implements ParticipationRepository {

	private final JpaParticipationRepository jpaParticipationRepository;
	private final ParticipationQueryDslRepository participationQueryRepository;

	@Override
	public Participation save(Participation participation) {
		return jpaParticipationRepository.save(participation);
	}

	@Override
	public List<Participation> findAllBySurveyIdIn(List<Long> surveyIds) {
		return jpaParticipationRepository.findAllBySurveyIdInAndIsDeleted(surveyIds, false);
	}

	@Override
	public Optional<Participation> findById(Long participationId) {
		return jpaParticipationRepository.findByIdAndIsDeletedFalse(participationId);
	}

	@Override
	public boolean exists(Long surveyId, Long userId) {
		return jpaParticipationRepository.existsBySurveyIdAndUserIdAndIsDeletedFalse(surveyId, userId);
	}

	@Override
	public Page<ParticipationInfo> findParticipationInfos(Long userId, Pageable pageable) {
		return participationQueryRepository.findParticipationInfos(userId, pageable);
	}

	@Override
	public Map<Long, Long> countsBySurveyIds(List<Long> surveyIds) {
		return participationQueryRepository.countsBySurveyIds(surveyIds);
	}

	@Override
	public List<ParticipationProjection> findParticipationProjectionsBySurveyIds(
		List<Long> surveyIds) {
		return participationQueryRepository.findParticipationProjectionsBySurveyIds(surveyIds);
	}

}