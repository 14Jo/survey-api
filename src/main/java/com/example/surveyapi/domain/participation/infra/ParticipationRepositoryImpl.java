package com.example.surveyapi.domain.participation.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.domain.participation.infra.jpa.JpaParticipationRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class ParticipationRepositoryImpl implements ParticipationRepository {

	private final JpaParticipationRepository jpaParticipationRepository;

	@Override
	public Participation save(Participation participation) {
		return jpaParticipationRepository.save(participation);
	}

	@Override
	public Page<Participation> findAll(Long memberId, Pageable pageable) {
		return jpaParticipationRepository.findAllByMemberIdAndIsDeleted(memberId, false, pageable);
	}
}
