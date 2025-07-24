package com.example.surveyapi.domain.participation.application.dto.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateParticipationRequest {

	private List<ResponseData> responseDataList;
}

