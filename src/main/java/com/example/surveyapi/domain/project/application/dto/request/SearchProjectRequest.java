package com.example.surveyapi.domain.project.application.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchProjectRequest {
	@Size(min = 3, message = "검색어는 최소 3글자 이상이어야 합니다.")
	private String keyword;
}