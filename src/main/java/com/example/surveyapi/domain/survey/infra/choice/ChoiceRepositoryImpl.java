package com.example.surveyapi.domain.survey.infra.choice;

import com.example.surveyapi.domain.survey.domain.choice.Choice;
import com.example.surveyapi.domain.survey.domain.choice.ChoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ChoiceRepositoryImpl implements ChoiceRepository {
    private final JpaChoiceRepository jpaRepository;

    @Override
    public Choice save(Choice choice) {
        return jpaRepository.save(choice);
    }
} 