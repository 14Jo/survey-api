package com.example.surveyapi.domain.participation.infra.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.participation.domain.participation.Participation;

public interface JpaParticipationRepository extends JpaRepository<Participation, Long> {
	Optional<Participation> findByIdAndIsDeletedFalse(Long id);

	boolean existsBySurveyIdAndUserIdAndIsDeletedFalse(Long surveyId, Long userId);
}
