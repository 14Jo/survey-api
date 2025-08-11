package com.example.surveyapi.domain.survey.application.qeury;

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

	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveyByProjectId(Long projectId, Long lastSurveyId) {
		List<SurveyReadEntity> surveyReadEntities;
		int pageSize = 20;

		if (lastSurveyId != null) {
			surveyReadEntities = surveyReadRepository.findByProjectIdAndSurveyIdGreaterThanOrderByCreatedAtDesc(
				projectId, lastSurveyId, PageRequest.of(0, pageSize));
		} else {
			surveyReadEntities = surveyReadRepository.findByProjectIdOrderByCreatedAtDesc(
				projectId, PageRequest.of(0, pageSize));
		}
		return surveyReadEntities.stream()
			.map(this::convertToSearchSurveyTitleResponse)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public SearchSurveyDetailResponse findSurveyDetailById(Long surveyId) {
		SurveyReadEntity surveyReadEntity = surveyReadRepository.findBySurveyId(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

		return SearchSurveyDetailResponse.from(surveyReadEntity, surveyReadEntity.getParticipationCount());
	}

	@Transactional(readOnly = true)
	public List<SearchSurveyTitleResponse> findSurveys(List<Long> surveyIds) {
		List<SurveyReadEntity> surveyReadEntities = surveyReadRepository.findBySurveyIdIn(surveyIds);

		return surveyReadEntities.stream()
			.map(this::convertToSearchSurveyTitleResponse)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public SearchSurveyStatusResponse findBySurveyStatus(String surveyStatus) {
		try {
			SurveyStatus status = SurveyStatus.valueOf(surveyStatus);
			List<SurveyReadEntity> surveyReadEntities = surveyReadRepository.findByStatus(status.name());

			List<Long> surveyIds = surveyReadEntities.stream()
				.map(SurveyReadEntity::getSurveyId)
				.collect(Collectors.toList());

			return SearchSurveyStatusResponse.from(surveyIds);
		} catch (IllegalArgumentException e) {
			throw new CustomException(CustomErrorCode.STATUS_INVALID_FORMAT);
		}
	}

	private SearchSurveyTitleResponse convertToSearchSurveyTitleResponse(SurveyReadEntity entity) {
		return SearchSurveyTitleResponse.from(entity);
	}
}
