package com.example.surveyapi.domain.survey.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyDtailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.query.QueryRepository;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SurveyQueryService {

	private final QueryRepository surveyQueryRepository;
	private final ParticipationPort port;

	//TODO 질문(선택지) 표시 순서 정렬 쿼리 작성
	@Transactional(readOnly = true)
	public SearchSurveyDtailResponse findSurveyDetailById(String authHeader, Long surveyId) {
		SurveyDetail surveyDetail = surveyQueryRepository.getSurveyDetail(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		Integer participationCount = port.getParticipationCounts(authHeader, List.of(surveyId))
			.getSurveyPartCounts().get(surveyId.toString());

		return SearchSurveyDtailResponse.from(surveyDetail, participationCount);
	}

	//TODO 참여수 연산 기능 구현 필요 있음
	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveyByProjectId(String authHeader, Long projectId, Long lastSurveyId) {

		List<SurveyTitle> surveyTitles = surveyQueryRepository.getSurveyTitles(projectId, lastSurveyId);
		List<Long> surveyIds = surveyTitles.stream().map(SurveyTitle::getSurveyId).collect(Collectors.toList());
		Map<String, Integer> partCounts = port.getParticipationCounts(authHeader, surveyIds).getSurveyPartCounts();

		return surveyTitles
			.stream()
			.map(response -> SearchSurveyTitleResponse.from(response, partCounts.get(response.getSurveyId().toString())))
			.toList();
	}
} 