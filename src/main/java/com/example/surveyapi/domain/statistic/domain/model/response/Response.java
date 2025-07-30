package com.example.surveyapi.domain.statistic.domain.model.response;

import java.util.stream.Stream;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;

public interface Response {
	Stream<Statistic.ChoiceIdentifier> getIdentifiers();
}
