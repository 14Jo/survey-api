package com.example.surveyapi.global.event;

import java.util.List;

import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.global.model.ParticipationEvent;

import lombok.Getter;

@Getter
public class ParticipationCreatedEvent implements ParticipationEvent {

	private final Long participationId;
	private final Long surveyId;
	private final List<ResponseData> responseDataList;

	public ParticipationCreatedEvent(Long participationId, Long surveyId, List<ResponseData> responseDataList) {
		this.participationId = participationId;
		this.surveyId = surveyId;
		this.responseDataList = responseDataList;
	}
}
