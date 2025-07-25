package com.example.surveyapi.domain.participation.application.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class CreateParticipationRequest {

	@NotEmpty(message = "응답 데이터는 최소 1개 이상이어야 합니다.")
	private List<ResponseData> responseDataList;
}

