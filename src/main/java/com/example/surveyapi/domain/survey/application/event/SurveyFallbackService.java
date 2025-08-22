package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.survey.SurveyEndDueEvent;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.example.surveyapi.global.event.survey.SurveyEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyFallbackService {

	private final SurveyRepository surveyRepository;

	@Transactional
	public void handleFailedEvent(SurveyEvent event, String routingKey, String failureReason) {
		try {
			switch (routingKey) {
				case RabbitConst.ROUTING_KEY_SURVEY_START_DUE:
					handleFailedSurveyStart((SurveyStartDueEvent) event, failureReason);
					break;
				case RabbitConst.ROUTING_KEY_SURVEY_END_DUE:
					handleFailedSurveyEnd((SurveyEndDueEvent) event, failureReason);
					break;
				default:
					log.warn("알 수 없는 라우팅 키: {}", routingKey);
			}
		} catch (Exception e) {
			log.error("풀백 처리 중 오류: {}", e.getMessage(), e);
		}
	}

	private void handleFailedSurveyStart(SurveyStartDueEvent event, String failureReason) {
		Long surveyId = event.getSurveyId();
		LocalDateTime scheduledTime = event.getScheduledAt();
		LocalDateTime now = LocalDateTime.now();
		
		log.error("설문 시작 이벤트 실패: surveyId={}, scheduledTime={}, reason={}", 
			surveyId, scheduledTime, failureReason);

		Optional<Survey> surveyOpt = surveyRepository.findById(surveyId);
		if (surveyOpt.isEmpty()) {
			log.error("설문을 찾을 수 없음: surveyId={}", surveyId);
			return;
		}

		Survey survey = surveyOpt.get();
		
		// 시간이 지났다면 즉시 시작
		if (scheduledTime.isBefore(now) && survey.getStatus() == SurveyStatus.PREPARING) {
			log.info("설문 시작 시간이 지났으므로 즉시 시작: surveyId={}", surveyId);
			survey.applyDurationChange(survey.getDuration(), now);
			surveyRepository.save(survey);
			log.info("설문 시작 풀백 완료: surveyId={}", surveyId);
		} else {
			log.warn("설문 시작 풀백 불가: surveyId={}, status={}, scheduledTime={}", 
				surveyId, survey.getStatus(), scheduledTime);
		}
	}

	private void handleFailedSurveyEnd(SurveyEndDueEvent event, String failureReason) {
		Long surveyId = event.getSurveyId();
		LocalDateTime scheduledTime = event.getScheduledAt();
		LocalDateTime now = LocalDateTime.now();
		
		log.error("설문 종료 이벤트 실패: surveyId={}, scheduledTime={}, reason={}", 
			surveyId, scheduledTime, failureReason);

		Optional<Survey> surveyOpt = surveyRepository.findById(surveyId);
		if (surveyOpt.isEmpty()) {
			log.error("설문을 찾을 수 없음: surveyId={}", surveyId);
			return;
		}

		Survey survey = surveyOpt.get();
		
		// 시간이 지났다면 즉시 종료
		if (scheduledTime.isBefore(now) && survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			log.info("설문 종료 시간이 지났으므로 즉시 종료: surveyId={}", surveyId);
			survey.applyDurationChange(survey.getDuration(), now);
			surveyRepository.save(survey);
			log.info("설문 종료 풀백 완료: surveyId={}", surveyId);
		} else {
			log.warn("설문 종료 풀백 불가: surveyId={}, status={}, scheduledTime={}", 
				surveyId, survey.getStatus(), scheduledTime);
		}
	}
}