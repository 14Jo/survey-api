package com.example.surveyapi.domain.survey.domain.choice;

import java.util.List;

public interface ChoiceRepository {

	Choice save(Choice choice);

	void saveAll(List<Choice> choices);

} 