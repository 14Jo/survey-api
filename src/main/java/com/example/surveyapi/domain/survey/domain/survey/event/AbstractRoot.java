package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.example.surveyapi.global.model.BaseEntity;
import com.example.surveyapi.global.model.SurveyEvent;

import jakarta.persistence.Transient;

public abstract class AbstractRoot extends BaseEntity {

	@Transient
	private final List<SurveyEvent> surveyEvents = new ArrayList<>();

	protected void registerEvent(SurveyEvent event) {
		this.surveyEvents.add(event);
	}

	public List<SurveyEvent> pollAllEvents() {
		if (surveyEvents.isEmpty()) {
			return Collections.emptyList();
		}
		List<SurveyEvent> events = new ArrayList<>(this.surveyEvents);
		this.surveyEvents.clear();
		return events;
	}

	public void setCreateEventId(Long surveyId) {
		for (SurveyEvent event : this.surveyEvents) {
			if (event instanceof SurveyCreatedEvent createdEvent) {
				createdEvent.setSurveyId(surveyId);
				break;
			}
		}
	}
}