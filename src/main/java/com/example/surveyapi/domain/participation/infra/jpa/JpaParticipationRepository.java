package com.example.surveyapi.domain.participation.infra.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.participation.domain.participation.Participation;

public interface JpaParticipationRepository extends JpaRepository<Participation, Long> {
	Page<Participation> findAllByMemberIdAndIsDeleted(Long memberId, Boolean isDeleted, Pageable pageable);

	List<Participation> findAllBySurveyIdInAndIsDeleted(List<Long> surveyIds, Boolean isDeleted);
}
