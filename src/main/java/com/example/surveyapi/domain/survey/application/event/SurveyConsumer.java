package com.example.surveyapi.domain.survey.application.event;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.command.SurveyService;
import com.example.surveyapi.domain.survey.domain.dlq.DeadLetterQueue;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.survey.SurveyEndDueEvent;
import com.example.surveyapi.global.event.survey.SurveyStartDueEvent;
import com.example.surveyapi.global.event.project.ProjectDeletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(
	queues = RabbitConst.QUEUE_NAME_SURVEY
)
public class SurveyConsumer {

	private final SurveyService surveyService;
	private final SurveyRepository surveyRepository;
	private final ObjectMapper objectMapper;

	@RabbitHandler
	public void handleProjectClosed(ProjectDeletedEvent event) {
		try {
			log.info("이벤트 수신");
			List<Survey> surveyOp = surveyRepository.findAllByProjectId(event.getProjectId());

			if (surveyOp.isEmpty())
				return;

			for (Survey survey : surveyOp) {
				surveyService.surveyDeleter(survey, survey.getSurveyId());
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@RabbitHandler
	@Transactional
	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2.0)
	)
	public void handleSurveyStart(SurveyStartDueEvent event) {
		try {
			log.info("SurveyStartDueEvent 수신: surveyId={}, scheduledAt={}", event.getSurveyId(),
				event.getScheduledAt());
			processSurveyStart(event);
		} catch (Exception e) {
			log.error("SurveyStartDueEvent 처리 실패: surveyId={}, error={}", event.getSurveyId(), e.getMessage());
			throw e;
		}
	}

	private void processSurveyStart(SurveyStartDueEvent event) {
		Optional<Survey> surveyOp = surveyRepository.findBySurveyIdAndIsDeletedFalse(event.getSurveyId());

		if (surveyOp.isEmpty())
			return;

		Survey survey = surveyOp.get();
		if (survey.getDuration().getStartDate() == null ||
			isDifferentMinute(survey.getDuration().getStartDate(), event.getScheduledAt())) {
			return;
		}

		if (survey.getStatus() == SurveyStatus.PREPARING) {
			survey.open();
			surveyRepository.stateUpdate(survey);
		}
	}

	@RabbitHandler
	@Transactional
	@Retryable(
		retryFor = {Exception.class},
		maxAttempts = 3,
		backoff = @Backoff(delay = 1000, multiplier = 2.0)
	)
	public void handleSurveyEnd(SurveyEndDueEvent event) {
		try {
			log.info("SurveyEndDueEvent 수신: surveyId={}, scheduledAt={}", event.getSurveyId(), event.getScheduledAt());
			processSurveyEnd(event);
		} catch (Exception e) {
			log.error("SurveyEndDueEvent 처리 실패: surveyId={}, error={}", event.getSurveyId(), e.getMessage());
			throw e;
		}
	}

	private void processSurveyEnd(SurveyEndDueEvent event) {
		Optional<Survey> surveyOp = surveyRepository.findBySurveyIdAndIsDeletedFalse(event.getSurveyId());

		if (surveyOp.isEmpty())
			return;

		Survey survey = surveyOp.get();
		if (survey.getDuration().getEndDate() == null ||
			isDifferentMinute(survey.getDuration().getEndDate(), event.getScheduledAt())) {
			return;
		}

		if (survey.getStatus() == SurveyStatus.IN_PROGRESS) {
			survey.close();
			surveyRepository.stateUpdate(survey);
		}
	}

	@Recover
	public void recoverSurveyStart(Exception ex, SurveyStartDueEvent event) {
		log.error("SurveyStartDueEvent 최종 실패 - DLQ 저장: surveyId={}, error={}", event.getSurveyId(), ex.getMessage());
		saveToDlq("survey.start.due", "SurveyStartDueEvent", event, ex.getMessage(), 3);
	}

	@Recover
	public void recoverSurveyEnd(Exception ex, SurveyEndDueEvent event) {
		log.error("SurveyEndDueEvent 최종 실패 - DLQ 저장: surveyId={}, error={}", event.getSurveyId(), ex.getMessage());
		saveToDlq("survey.end.due", "SurveyEndDueEvent", event, ex.getMessage(), 3);
	}

	private void saveToDlq(String routingKey, String queueName, Object event, String errorMessage, Integer retryCount) {
		try {
			String messageBody = objectMapper.writeValueAsString(event);
			DeadLetterQueue dlq = DeadLetterQueue.create(queueName, routingKey, messageBody, errorMessage, retryCount);
			log.info("DLQ 저장 완료: routingKey={}, queueName={}", routingKey, queueName);
		} catch (Exception e) {
			log.error("DLQ 저장 실패: routingKey={}, error={}", routingKey, e.getMessage());
		}
	}

	private boolean isDifferentMinute(LocalDateTime activeDate, LocalDateTime scheduledDate) {
		return !activeDate.truncatedTo(ChronoUnit.MINUTES).isEqual(scheduledDate.truncatedTo(ChronoUnit.MINUTES));
	}
}
