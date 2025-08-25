package com.example.surveyapi.participation.application;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.example.surveyapi.participation.application.client.SurveyInfoDto;
import com.example.surveyapi.participation.application.client.SurveyServicePort;
import com.example.surveyapi.participation.application.dto.response.ParticipationDetailResponse;
import com.example.surveyapi.participation.application.dto.response.ParticipationGroupResponse;
import com.example.surveyapi.participation.application.dto.response.ParticipationInfoResponse;
import com.example.surveyapi.participation.domain.participation.ParticipationRepository;
import com.example.surveyapi.participation.domain.participation.query.ParticipationInfo;
import com.example.surveyapi.participation.domain.participation.query.ParticipationProjection;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@Service
public class ParticipationQueryService {

	private final ParticipationRepository participationRepository;
	private final SurveyServicePort surveyPort;
	private final TransactionTemplate readTransactionTemplate;

	public ParticipationQueryService(ParticipationRepository participationRepository, SurveyServicePort surveyPort,
		PlatformTransactionManager transactionManager) {
		this.participationRepository = participationRepository;
		this.surveyPort = surveyPort;
		this.readTransactionTemplate = new TransactionTemplate(transactionManager);
		this.readTransactionTemplate.setReadOnly(true);
	}

	public Page<ParticipationInfoResponse> gets(String authHeader, Long userId, Pageable pageable) {
		Page<ParticipationInfo> participationInfos = readTransactionTemplate.execute(status ->
			participationRepository.findParticipationInfos(userId, pageable)
		);

		if (participationInfos == null || participationInfos.isEmpty()) {
			return Page.empty(pageable);
		}

		List<Long> surveyIds = participationInfos.getContent().stream()
			.map(ParticipationInfo::getSurveyId)
			.distinct()
			.toList();

		List<SurveyInfoDto> surveyInfoList = surveyPort.getSurveyInfoList(authHeader, surveyIds);

		List<ParticipationInfoResponse.SurveyInfoOfParticipation> surveyInfoOfParticipations = surveyInfoList.stream()
			.map(ParticipationInfoResponse.SurveyInfoOfParticipation::from)
			.toList();

		Map<Long, ParticipationInfoResponse.SurveyInfoOfParticipation> surveyInfoMap = surveyInfoOfParticipations.stream()
			.collect(Collectors.toMap(
				ParticipationInfoResponse.SurveyInfoOfParticipation::getSurveyId,
				surveyInfo -> surveyInfo
			));

		return participationInfos.map(p -> {
			ParticipationInfoResponse.SurveyInfoOfParticipation surveyInfo = surveyInfoMap.get(p.getSurveyId());

			return ParticipationInfoResponse.of(p, surveyInfo);
		});
	}

	@Transactional(readOnly = true)
	public List<ParticipationGroupResponse> getAllBySurveyIds(List<Long> surveyIds) {
		List<ParticipationProjection> projections = participationRepository.findParticipationProjectionsBySurveyIds(
			surveyIds);

		// surveyId 기준으로 참여 기록을 Map 으로 그룹핑
		Map<Long, List<ParticipationProjection>> participationGroupBySurveyId = projections.stream()
			.collect(Collectors.groupingBy(ParticipationProjection::getSurveyId));

		List<ParticipationGroupResponse> result = new ArrayList<>();

		for (Long surveyId : surveyIds) {
			List<ParticipationProjection> participationGroup = participationGroupBySurveyId.getOrDefault(surveyId,
				Collections.emptyList());

			List<ParticipationDetailResponse> participationDtos = participationGroup.stream()
				.map(ParticipationDetailResponse::fromProjection)
				.toList();

			result.add(ParticipationGroupResponse.of(surveyId, participationDtos));
		}
		return result;
	}

	@Transactional(readOnly = true)
	public ParticipationDetailResponse get(Long userId, Long participationId) {
		return participationRepository.findParticipationProjectionByIdAndUserId(participationId, userId)
			.map(ParticipationDetailResponse::fromProjection)
			.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_PARTICIPATION));
	}

	@Transactional(readOnly = true)
	public Map<Long, Long> getCountsBySurveyIds(List<Long> surveyIds) {
		return participationRepository.countsBySurveyIds(surveyIds);
	}
}
