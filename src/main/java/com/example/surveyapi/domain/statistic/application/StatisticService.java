package com.example.surveyapi.domain.statistic.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.statistic.application.client.ParticipationInfoDto;
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
		List<Long> surveyIds = List.of(1L, 2L, 3L);

		List<ParticipationInfoDto> participationInfos =
			participationServicePort.getParticipationInfos(authHeader, surveyIds);

		log.info("participationInfos: {}", participationInfos);
		participationInfos.forEach(info -> {
			if(info.participations().isEmpty()){
				return;
			}
			Statistic statistic = getStatistic(info.surveyId());

			//TODO : 새로운거만 받아오는 방법 고민
			List<ParticipationInfoDto.ParticipationDetailDto> newInfo = info.participations().stream()
				.filter(p -> p.participationId() > statistic.getLastProcessedParticipationId())
				.toList();

			if (newInfo.isEmpty()) {
				log.info("새로운 응답이 없습니다. surveyId: {}", info.surveyId());
				return;
			}

			StatisticCommand command = toStatisticCommand(newInfo);
			statistic.calculate(command);

			Long maxId = newInfo.stream()
				.map(ParticipationInfoDto.ParticipationDetailDto::participationId)
				.max(Long::compareTo)
				.orElse(null);

			statistic.updateLastProcessedId(maxId);
			statisticRepository.save(statistic);
		});
	}

	public Statistic getStatistic(Long surveyId) {
		return statisticRepository.findById(surveyId)
			.orElseThrow(() -> new CustomException(CustomErrorCode.STATISTICS_NOT_FOUND));
	}

	private StatisticCommand toStatisticCommand(List<ParticipationInfoDto.ParticipationDetailDto> participations) {
		List<StatisticCommand.ParticipationDetailData> detail = participations.stream()
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
