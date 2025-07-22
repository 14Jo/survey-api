package com.example.surveyapi.domain.survey.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.survey.domain.Survey;

public interface JpaSurveyRepository extends JpaRepository<Survey,Long> {
}
