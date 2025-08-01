package com.example.surveyapi.domain.statistic.application;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.statistic.application.client.ParticipationInfoDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationRequestDto;
import com.example.surveyapi.domain.statistic.application.client.ParticipationServicePort;
import com.example.surveyapi.domain.statistic.domain.dto.StatisticCommand;
import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;
import com.example.surveyapi.domain.statistic.domain.repository.StatisticRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatisticService {

	private final StatisticRepository statisticRepository;
	private final ParticipationServicePort participationServicePort;

	@Transactional
	public void create(Long surveyId) {
		//TODO : survey 유효성 검사
		//TODO : survey 이벤트 수신
		if (statisticRepository.existsById(surveyId)) {
			throw new CustomException(CustomErrorCode.STATISTICS_ALREADY_EXISTS);
		}
		Statistic statistic = Statistic.create(surveyId);
		statisticRepository.save(statistic);
	}

	@Transactional
	//@Scheduled(cron = "0 */5 * * * *")
	public void calculateLiveStatistics(String authHeader) {
		//TODO : Survey 도메인으로 부터 진행중인 설문 Id List 받아오기
		List<Long> surveyIds = new ArrayList<>();
		surveyIds.add(1L);
		// surveyIds.add(2L);
		// surveyIds.add(3L);

		ParticipationRequestDto request = new ParticipationRequestDto(surveyIds);
		List<ParticipationInfoDto> participationInfos = participationServicePort.getParticipationInfos(authHeader, request);
		log.info("participationInfos: {}", participationInfos);

		participationInfos.forEach(info -> {
			Statistic statistic = getStatistic(info.surveyId());
			StatisticCommand command = toStatisticCommand(info);
			statistic.calculate(command);
			statisticRepository.save(statistic);
		});
	}

	private Statistic getStatistic(Long surveyId) {
		return statisticRepository.findById(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.STATISTICS_NOT_FOUND));
	}

	private StatisticCommand toStatisticCommand(ParticipationInfoDto info) {
		List<StatisticCommand.ParticipationDetailData> detail =
			info.participations().stream()
				.map(participation -> new StatisticCommand.ParticipationDetailData(
					participation.participatedAt(),
					participation.responses().stream()
						.map(response -> new StatisticCommand.ResponseData(
							response.questionId(), response.answer()
						)).toList()
				)).toList();
		return new StatisticCommand(detail);
	}
}
