package com.example.surveyapi.domain.survey.infra.survey.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.survey.domain.survey.Survey;

public interface JpaSurveyRepository extends JpaRepository<Survey,Long> {
}
