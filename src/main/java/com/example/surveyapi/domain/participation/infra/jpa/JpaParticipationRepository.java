package com.example.surveyapi.domain.participation.infra.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.participation.domain.participation.Participation;

public interface JpaParticipationRepository extends JpaRepository<Participation, Long> {
	Page<Participation> findAllByMemberIdAndIsDeleted(Long memberId, Boolean isDeleted, Pageable pageable);

	@Query("SELECT p FROM Participation p JOIN FETCH p.responses WHERE p.surveyId IN :surveyIds AND p.isDeleted = :isDeleted")
	List<Participation> findAllBySurveyIdInAndIsDeleted(@Param("surveyIds") List<Long> surveyIds,
		@Param("isDeleted") Boolean isDeleted);

	@Query("SELECT p FROM Participation p JOIN FETCH p.responses WHERE p.id = :id AND p.isDeleted = FALSE")
	Optional<Participation> findWithResponseByIdAndIsDeletedFalse(@Param("id") Long id);
}
