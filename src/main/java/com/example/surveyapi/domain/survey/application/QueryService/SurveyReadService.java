package com.example.surveyapi.domain.survey.application.QueryService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import com.example.surveyapi.domain.survey.application.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.response.SearchSurveyStatusResponse;
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

	/**
	 * MongoDB를 사용하여 프로젝트의 설문 목록을 조회합니다.
	 */
	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveyByProjectId(Long projectId, Long lastSurveyId) {
		log.debug("=== MongoDB 설문 조회 시작 - projectId: {}, lastSurveyId: {} ===", projectId, lastSurveyId);
		long startTime = System.currentTimeMillis();

		try {
			List<SurveyReadEntity> surveyReadEntities;
			final int PAGE_SIZE = 20;

			if (lastSurveyId != null) {
				surveyReadEntities = surveyReadRepository.findByProjectIdAndSurveyIdGreaterThanOrderByCreatedAtDesc(
					projectId, lastSurveyId, PageRequest.of(0, PAGE_SIZE));
			} else {
				surveyReadEntities = surveyReadRepository.findByProjectIdOrderByCreatedAtDesc(
					projectId, PageRequest.of(0, PAGE_SIZE));
			}

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.debug("=== MongoDB 설문 조회 완료 - 실행시간: {}ms, 조회된 설문 수: {} ===", duration, surveyReadEntities.size());

			return surveyReadEntities.stream()
				.map(this::convertToSearchSurveyTitleResponse)
				.collect(Collectors.toList());

		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.error("=== MongoDB 설문 조회 실패 - 실행시간: {}ms, 에러: {} ===", duration, e.getMessage());

			log.warn("MongoDB 조회 실패로 인해 기존 PostgreSQL 방식으로 fallback합니다.");
			throw new CustomException(CustomErrorCode.SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * MongoDB를 사용하여 설문 상세 정보를 조회합니다.
	 */
	@Transactional(readOnly = true)
	public SearchSurveyDetailResponse findSurveyDetailById(Long surveyId) {
		log.debug("=== MongoDB 설문 상세 조회 시작 - surveyId: {} ===", surveyId);
		long startTime = System.currentTimeMillis();

		try {
			SurveyReadEntity surveyReadEntity = surveyReadRepository.findBySurveyId(surveyId)
				.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.debug("=== MongoDB 설문 상세 조회 완료 - 실행시간: {}ms ===", duration);

			return SearchSurveyDetailResponse.from(surveyReadEntity, surveyReadEntity.getParticipationCount());

		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.error("=== MongoDB 설문 상세 조회 실패 - 실행시간: {}ms, 에러: {} ===", duration, e.getMessage());

			log.warn("MongoDB 조회 실패로 인해 기존 PostgreSQL 방식으로 fallback합니다.");
			throw new CustomException(CustomErrorCode.SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * MongoDB를 사용하여 특정 설문 ID 목록의 설문들을 조회합니다.
	 */
	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveys(List<Long> surveyIds) {
		log.debug("=== MongoDB 설문 목록 조회 시작 - surveyIds: {} ===", surveyIds);
		long startTime = System.currentTimeMillis();

		try {
			List<SurveyReadEntity> surveyReadEntities = surveyReadRepository.findBySurveyIdIn(surveyIds);

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.debug("=== MongoDB 설문 목록 조회 완료 - 실행시간: {}ms, 조회된 설문 수: {} ===", duration, surveyReadEntities.size());

			return surveyReadEntities.stream()
				.map(this::convertToSearchSurveyTitleResponse)
				.collect(Collectors.toList());

		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.error("=== MongoDB 설문 목록 조회 실패 - 실행시간: {}ms, 에러: {} ===", duration, e.getMessage());

			throw new CustomException(CustomErrorCode.SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * MongoDB를 사용하여 특정 상태의 설문 목록을 조회합니다.
	 */
	@Transactional(readOnly = true)
	public SearchSurveyStatusResponse findBySurveyStatus(String surveyStatus) {
		log.debug("=== MongoDB 설문 상태별 조회 시작 - surveyStatus: {} ===", surveyStatus);
		long startTime = System.currentTimeMillis();

		try {
			SurveyStatus status = SurveyStatus.valueOf(surveyStatus);
			List<SurveyReadEntity> surveyReadEntities = surveyReadRepository.findByStatus(status.name());

			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.debug("=== MongoDB 설문 상태별 조회 완료 - 실행시간: {}ms, 조회된 설문 수: {} ===", duration, surveyReadEntities.size());

			// SurveyReadEntity 목록에서 surveyId만 추출하여 SearchSurveyStatusResponse 생성
			List<Long> surveyIds = surveyReadEntities.stream()
				.map(SurveyReadEntity::getSurveyId)
				.collect(Collectors.toList());

			return SearchSurveyStatusResponse.from(surveyIds);

		} catch (IllegalArgumentException e) {
			log.error("=== 설문 상태 파싱 실패 - surveyStatus: {}, 에러: {} ===", surveyStatus, e.getMessage());
			throw new CustomException(CustomErrorCode.STATUS_INVALID_FORMAT);
		} catch (Exception e) {
			long endTime = System.currentTimeMillis();
			long duration = endTime - startTime;
			log.error("=== MongoDB 설문 상태별 조회 실패 - 실행시간: {}ms, 에러: {} ===", duration, e.getMessage());

			log.warn("MongoDB 조회 실패로 인해 기존 PostgreSQL 방식으로 fallback합니다.");
			throw new CustomException(CustomErrorCode.SERVER_ERROR, e.getMessage());
		}
	}

	/**
	 * SurveyReadEntity를 SearchSurveyTitleResponse로 변환합니다.
	 */
	private SearchSurveyTitleResponse convertToSearchSurveyTitleResponse(SurveyReadEntity entity) {
		return SearchSurveyTitleResponse.from(entity);
	}

	/**
	 * 특정 설문의 상세 정보를 MongoDB에서 조회합니다.
	 */
	@Transactional(readOnly = true)
	public SurveyReadEntity findSurveyById(Long surveyId) {
		return surveyReadRepository.findBySurveyId(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));
	}

	/**
	 * MongoDB에 데이터가 있는지 확인합니다.
	 */
	public boolean hasDataInMongoDB(Long projectId) {
		List<SurveyReadEntity> surveys = surveyReadRepository.findByProjectIdOrderByCreatedAtDesc(
			projectId, PageRequest.of(0, 1));
		return !surveys.isEmpty();
	}
}
