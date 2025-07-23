package com.example.surveyapi.domain.survey.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.response.SearchSurveyDtailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.query.QueryRepository;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyQueryService {

	private final QueryRepository surveyQueryRepository;

	@Transactional(readOnly = true)
	public SearchSurveyDtailResponse findSurveyDetailById(Long surveyId) {
		SurveyDetail surveyDetail = surveyQueryRepository.getSurveyDetail(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		return new SearchSurveyDtailResponse(surveyDetail.getTitle(), surveyDetail.getDescription(),
			surveyDetail.getDuration(), surveyDetail.getOption(), surveyDetail.getQuestions());
	}

	//TODO 참여수 연산 기능 구현 필요 있음
	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveyByProjectId(Long projectId, Long lastSurveyId) {

		return surveyQueryRepository.getSurveyTitles(projectId, lastSurveyId)
			.stream()
			.map(surveyTitle ->
				new SearchSurveyTitleResponse(
					surveyTitle.getSurveyId(),
					surveyTitle.getTitle(),
					surveyTitle.getStatus(),
					surveyTitle.getDuration()
				)
			)
			.toList();
	}
} 