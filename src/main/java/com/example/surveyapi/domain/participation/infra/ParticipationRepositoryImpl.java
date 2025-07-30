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
import com.example.surveyapi.domain.participation.infra.dsl.ParticipationQueryRepositoryImpl;
import com.example.surveyapi.domain.participation.infra.jpa.JpaParticipationRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ParticipationRepositoryImpl implements ParticipationRepository {

	private final JpaParticipationRepository jpaParticipationRepository;
	private final ParticipationQueryRepositoryImpl participationQueryRepository;

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
		return jpaParticipationRepository.findWithResponseByIdAndIsDeletedFalse(participationId);
	}

	@Override
	public Page<ParticipationInfo> findParticipationsInfo(Long memberId, Pageable pageable) {
		return participationQueryRepository.findParticipationsInfo(memberId, pageable);
	}

	@Override
	public Map<Long, Long> countsBySurveyIds(List<Long> surveyIds) {
		return participationQueryRepository.countsBySurveyIds(surveyIds);
	}
}