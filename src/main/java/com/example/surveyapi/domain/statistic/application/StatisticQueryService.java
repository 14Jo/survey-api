package com.example.surveyapi.domain.statistic.application;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.statistic.application.client.ParticipationServicePort;
import com.example.surveyapi.domain.statistic.application.client.SurveyDetailDto;
import com.example.surveyapi.domain.statistic.application.client.SurveyServicePort;
import com.example.surveyapi.domain.statistic.application.dto.response.StatisticDetailResponse;
import com.example.surveyapi.domain.statistic.domain.StatisticReport;
import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;
import com.example.surveyapi.domain.statistic.domain.model.entity.StatisticsItem;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;
import com.example.surveyapi.domain.statistic.domain.repository.StatisticQueryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticQueryService {

	private final StatisticQueryRepository statisticQueryRepository;
	private final StatisticService statisticService;

	private final ParticipationServicePort participationServicePort;
	private final SurveyServicePort surveyServicePort;

	public StatisticDetailResponse getLiveStatistics(String authHeader, Long surveyId) {
		//통계 전체 가져오기
		Statistic statistic = statisticService.getStatistic(surveyId);
		List<StatisticsItem> responses = statistic.getResponses();

		//설문 가져오기 & 정렬 (정렬해서 가져오면??)
		SurveyDetailDto surveyDetail = surveyServicePort.getSurveyDetail(authHeader, surveyId);

		if (responses.isEmpty()) {
			return StatisticDetailResponse.of(
				StatisticReport.from(List.of()),
				surveyDetail,
				statistic,
				List.of(),
				List.of()
			);
		}
		// questions 정렬
		List<SurveyDetailDto.QuestionInfo> sortedQuestions = surveyDetail.questions().stream()
			.sorted(Comparator.comparingInt(SurveyDetailDto.QuestionInfo::displayOrder))
			.toList();

		// 서술형 questionId 추출
		List<Long> textQuestionIds = sortedQuestions.stream()
			.filter(q -> q.questionType() == null || q.choices().isEmpty())
			.map(SurveyDetailDto.QuestionInfo::questionId)
			.toList();
		//서술형 질문 응답 가져오기
		Map<Long, List<String>> textAnswers = participationServicePort.getTextAnswersByQuestionIds(authHeader, textQuestionIds);
		log.info(textAnswers.toString());

		StatisticReport report = StatisticReport.from(responses);
		//TODO : 수정하기 -> 시간에 대한 참여수로

		// 시간별 응답수 매핑
		List<Map<String, Object>> temporalStats = report.mappingTemporalStat();
		List<StatisticDetailResponse.TemporalStat> temporalResponseList = StatisticDetailResponse.TemporalStat.toStats(temporalStats);

		// 문항별 응답수 매핑
		Map<Long, StatisticReport.QuestionStatsResult> questionStats = report.mappingQuestionStat();

		// 문항별 질문, 설명 매핑
		List<StatisticDetailResponse.QuestionStat> questionResponses = sortedQuestions.stream()
			.map(questionInfo -> {
				StatisticReport.QuestionStatsResult statResult = questionStats.get(questionInfo.questionId());
				if(statResult == null) {
					return null;
				}

				List<StatisticDetailResponse.ChoiceStat> choiceStats;
				if (AnswerType.TEXT_ANSWER.equals(AnswerType.findByKey(statResult.answerType()))) {
					// 서술형 응답 처리
					List<String> texts = textAnswers.getOrDefault(questionInfo.questionId(), List.of());
					choiceStats = new ArrayList<>(StatisticDetailResponse.TextStat.toStats(texts));
				} else {
					// 선택형 응답 처리
					List<StatisticReport.ChoiceStatsResult> choiceResults = statResult.choiceCounts();
					List<SurveyDetailDto.ChoiceInfo> choiceInfos = questionInfo.choices();
					choiceStats = new ArrayList<>(StatisticDetailResponse.SelectChoiceStat.toStats(choiceResults, choiceInfos));
				}

				return StatisticDetailResponse.QuestionStat.of(statResult, questionInfo, choiceStats);

			})
			.filter(Objects::nonNull)
			.toList();

		return StatisticDetailResponse.of(
			report,
			surveyDetail,
			statistic,
			temporalResponseList,
			questionResponses
		);//질문타입에 따른 choice Stat만들기(합치거나 groupby한거 가져오기)
	}
}
