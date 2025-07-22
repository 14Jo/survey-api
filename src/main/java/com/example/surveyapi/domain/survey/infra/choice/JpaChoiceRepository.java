package com.example.surveyapi.domain.survey.infra.choice;

import com.example.surveyapi.domain.survey.domain.choice.Choice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChoiceRepository extends JpaRepository<Choice, Long> {
} 