package com.example.surveyapi.domain.survey.infra.choice;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.choice.Choice;
import com.example.surveyapi.domain.survey.domain.choice.ChoiceRepository;
import com.example.surveyapi.domain.survey.infra.choice.jpa.JpaChoiceRepository;

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

    @Override
    public void saveAll(List<Choice> choices) {
        jpaRepository.saveAll(choices);
    }
}