package com.example.surveyapi.domain.survey.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.statistic.application.client.ParticipationServicePort;
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
	private final ParticipationServicePort port;

	//TODO 질문(선택지) 표시 순서 정렬 쿼리 작성
	@Transactional(readOnly = true)
	public SearchSurveyDtailResponse findSurveyDetailById(Long surveyId) {
		SurveyDetail surveyDetail = surveyQueryRepository.getSurveyDetail(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		return SearchSurveyDtailResponse.from(surveyDetail);
	}

	//TODO 참여수 연산 기능 구현 필요 있음
	// @Transactional(readOnly = true)
	// public List<SearchSurveyTitleResponse> findSurveyByProjectId(String authHeader, Long projectId, Long lastSurveyId) {
	//
	// 	List<Long> surveyIds = new ArrayList<>();
	//
	// 	for (int i = lastSurveyId.intValue(); i > lastSurveyId.intValue() - 10; i--) {
	// 		surveyIds.add((long)i);
	// 	}
	//
	// 	//Map<Long, Integer> infos = port.getParticipationInfos(authHeader, surveyIds);
	//
	// 	return surveyQueryRepository.getSurveyTitles(projectId, lastSurveyId)
	// 		.stream()
	// 		.map(response -> SearchSurveyTitleResponse.from(response, infos.get(response.getSurveyId())))
	// 		.toList();
	// }
} 