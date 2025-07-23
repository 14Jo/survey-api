package com.example.surveyapi.domain.participation.infra.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.participation.domain.participation.Participation;

public interface JpaParticipationRepository extends JpaRepository<Participation, Long> {
}
