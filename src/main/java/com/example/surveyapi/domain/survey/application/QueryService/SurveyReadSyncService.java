package com.example.surveyapi.domain.survey.application.QueryService;

import java.util.List;
import java.util.Map;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.QueryService.dto.QuestionSyncDto;
import com.example.surveyapi.domain.survey.application.QueryService.dto.SurveySyncDto;
import com.example.surveyapi.domain.survey.application.client.ParticipationCountDto;
import com.example.surveyapi.domain.survey.application.client.ParticipationPort;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadRepository;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyReadSyncService {

	private final SurveyReadRepository surveyReadRepository;
	private final ParticipationPort partPort;

	@Async
	@Transactional
	public void surveyReadSync(SurveySyncDto dto) {
		try {
			log.debug("설문 조회 테이블 동기화 시작");

			SurveySyncDto.SurveyOptions options = dto.getOptions();
			SurveyReadEntity.SurveyOptions surveyOptions = new SurveyReadEntity.SurveyOptions(options.isAnonymous(),
				options.isAllowResponseUpdate(), options.getStartDate(), options.getEndDate());

			SurveyReadEntity surveyRead = SurveyReadEntity.create(
				dto.getSurveyId(), dto.getProjectId(), dto.getTitle(),
				dto.getDescription(), dto.getStatus().name(), 0, surveyOptions
			);

			surveyReadRepository.save(surveyRead);
			log.debug("설문 조회 테이블 동기화 종료");

		} catch (Exception e) {
			log.error("설문 조회 테이블 동기화 실패 {}", e.getMessage());
		}
	}

	@Async
	@Transactional
	public void questionReadSync(Long surveyId, List<QuestionSyncDto> dtos) {
		try {
			log.debug("설문 조회 테이블 질문 동기화 시작");

			SurveyReadEntity surveyRead = surveyReadRepository.findBySurveyId(surveyId)
				.orElseThrow(() -> new CustomException(CustomErrorCode.NOT_FOUND_SURVEY));

			surveyRead.setQuestions(dtos.stream().map(dto -> {
				return new SurveyReadEntity.QuestionSummary(
					dto.getQuestionId(), dto.getContent(), dto.getType(),
					dto.isRequired(), dto.getDisplayOrder(),
					dto.getChoices()
						.stream()
						.map(choiceDto -> Choice.of(choiceDto.getContent(), choiceDto.getDisplayOrder()))
						.toList()
				);
			}).toList());
			surveyReadRepository.save(surveyRead);
			log.debug("설문 조회 테이블 질문 동기화 종료");

		} catch (Exception e) {
			log.error("설문 조회 테이블 질문 동기화 실패 {}", e.getMessage());
		}
	}

	@Scheduled(fixedRate = 300000)
	public void batchParticipationCountSync() {
		log.debug("참여자 수 조회 시작");
		List<SurveyReadEntity> surveys = surveyReadRepository.findAll();
		List<Long> surveyIds = surveys.stream().map(SurveyReadEntity::getSurveyId).toList();

		Map<String, Integer> surveyPartCounts = partPort.getParticipationCounts(surveyIds).getSurveyPartCounts();

		surveys.forEach(survey -> {
			if (surveyPartCounts.containsKey(survey.getSurveyId().toString())) {
				survey.updateParticipationCount(surveyPartCounts.get(survey.getSurveyId().toString()));
			}
		});

		surveyReadRepository.saveAll(surveys);
	}
}
