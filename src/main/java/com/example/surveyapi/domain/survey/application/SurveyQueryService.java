package com.example.surveyapi.domain.survey.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.response.SearchSurveyDtailResponse;
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

		return new SearchSurveyDtailResponse(surveyDetail.getTitle(), surveyDetail.getDescription(), surveyDetail.getDuration(), surveyDetail.getOption(), surveyDetail.getQuestions());
	}
} 