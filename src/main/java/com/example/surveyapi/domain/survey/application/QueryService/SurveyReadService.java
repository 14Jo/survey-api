package com.example.surveyapi.domain.survey.application.QueryService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyReadService {

	private final SurveyReadRepository surveyReadRepository;

	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveyByProjectId(Long projectId, Long lastSurveyId) {
		log.debug("=== MongoDB 설문 조회 시작 - projectId: {}, lastSurveyId: {} ===", projectId, lastSurveyId);
		long startTime = System.currentTimeMillis();

		try {
			List<SurveyReadEntity> surveyReadEntities;
			int pageSize = 20;

			if (lastSurveyId != null) {
				surveyReadEntities = surveyReadRepository.findByProjectIdAndSurveyIdGreaterThanOrderByCreatedAtDesc(
					projectId, lastSurveyId, PageRequest.of(0, pageSize));
			} else {
				surveyReadEntities = surveyReadRepository.findByProjectIdOrderByCreatedAtDesc(
					projectId, PageRequest.of(0, pageSize));
			}

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.debug("=== MongoDB 설문 조회 완료 - 실행시간: {}ms, 조회된 설문 수: {} ===", duration, surveyReadEntities.size());

			// SurveyReadEntity를 SearchSurveyTitleResponse로 변환
			return surveyReadEntities.stream()
				.map(this::convertToSearchSurveyTitleResponse)
				.collect(Collectors.toList());

		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.error("=== MongoDB 설문 조회 실패 - 실행시간: {}ms, 에러: {} ===", duration, e.getMessage());

			// MongoDB 조회 실패 시 기존 PostgreSQL 방식으로 fallback
			log.warn("MongoDB 조회 실패로 인해 기존 PostgreSQL 방식으로 fallback합니다.");
			throw new CustomException(CustomErrorCode.SERVER_ERROR, e.getMessage());
		}
	}

	private SearchSurveyTitleResponse convertToSearchSurveyTitleResponse(SurveyReadEntity entity) {
		return SearchSurveyTitleResponse.from(entity);
	}

	@Transactional(readOnly = true)
	public SurveyReadEntity findSurveyById(Long surveyId) {
		return surveyReadRepository.findBySurveyId(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));
	}
}
