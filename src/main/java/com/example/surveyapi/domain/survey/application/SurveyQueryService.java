package com.example.surveyapi.domain.survey.application;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyStatusResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.query.QueryRepository;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyStatusList;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyQueryService {

	private final QueryRepository surveyQueryRepository;
	private final ParticipationPort port;

	@Transactional(readOnly = true)
	public SearchSurveyDetailResponse findSurveyDetailById(String authHeader, Long surveyId) {
		SurveyDetail surveyDetail = surveyQueryRepository.getSurveyDetail(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		Integer participationCount = port.getParticipationCounts(List.of(surveyId))
			.getSurveyPartCounts().get(surveyId.toString());

		return SearchSurveyDetailResponse.from(surveyDetail, participationCount);
	}

	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveys(List<Long> surveyIds) {

		return surveyQueryRepository.getSurveys(surveyIds)
			.stream()
			.map(response -> SearchSurveyTitleResponse.from(response, null))
			.toList();
	}

	public SearchSurveyStatusResponse findBySurveyStatus(String surveyStatus) {
		try {
			SurveyStatus status = SurveyStatus.valueOf(surveyStatus);
			SurveyStatusList surveyStatusList = surveyQueryRepository.getSurveyStatusList(status);

			return SearchSurveyStatusResponse.from(surveyStatusList);

		} catch (IllegalArgumentException e) {
			throw new CustomException(CustomErrorCode.STATUS_INVALID_FORMAT);
		}
	}
}