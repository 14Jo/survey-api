package com.example.surveyapi.domain.participation.application.dto.request;

import java.util.List;

import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.enums.Gender;
import com.example.surveyapi.domain.participation.domain.participation.vo.Region;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateParticipationRequest {

	@NotEmpty(message = "응답 데이터는 최소 1개 이상이어야 합니다.")
	private List<ResponseData> responseDataList;

	@NotNull(message = "사용자 생년월일은 필수입니다.")
	private String birth;

	@NotNull(message = "사용자 성별은 필수입니다.")
	private Gender gender;

	@NotNull(message = "사용자 지역 정보는 필수입니다.")
	private Region region;
}

