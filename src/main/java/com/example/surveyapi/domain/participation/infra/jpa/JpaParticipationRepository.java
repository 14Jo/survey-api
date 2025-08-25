package com.example.surveyapi.domain.participation.infra.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.participation.domain.participation.Participation;

public interface JpaParticipationRepository extends JpaRepository<Participation, Long> {
	List<Participation> findAllBySurveyIdInAndIsDeleted(List<Long> surveyIds, Boolean isDeleted);

	Optional<Participation> findByIdAndIsDeletedFalse(Long id);

	boolean existsBySurveyIdAndUserIdAndIsDeletedFalse(Long surveyId, Long userId);
}
