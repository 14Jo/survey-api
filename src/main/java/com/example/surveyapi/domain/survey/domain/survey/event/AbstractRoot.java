package com.example.surveyapi.domain.survey.domain.survey.event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.BaseEntity;
import com.example.surveyapi.global.model.SurveyEvent;

import jakarta.persistence.Transient;

public abstract class AbstractRoot extends BaseEntity {

	@Transient
	private final Map<EventCode, List<SurveyEvent>> surveyEvents = new HashMap<>();

	protected void registerEvent(SurveyEvent event, EventCode key) {
		if (!this.surveyEvents.containsKey(key)) {
			this.surveyEvents.put(key, new ArrayList<>());
		}
		this.surveyEvents.get(key).add(event);
	}

	public Map<EventCode, List<SurveyEvent>> pollAllEvents() {
		if (surveyEvents.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<EventCode, List<SurveyEvent>> events = new HashMap<>(this.surveyEvents);
		this.surveyEvents.clear();
		return events;
	}
}