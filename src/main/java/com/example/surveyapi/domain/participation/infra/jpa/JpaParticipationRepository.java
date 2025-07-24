package com.example.surveyapi.domain.participation.infra.jpa;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.participation.domain.participation.Participation;

public interface JpaParticipationRepository extends JpaRepository<Participation, Long> {
	Page<Participation> findAllByMemberIdAndIsDeleted(Long memberId, Boolean isDeleted, Pageable pageable);

	@Query("SELECT p FROM Participation p JOIN FETCH p.responses WHERE p.id = :id")
	Optional<Participation> findWithResponseById(@Param("id") Long id);
}
