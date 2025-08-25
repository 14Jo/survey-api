package com.example.surveyapi.domain.statistic.infra.event;

import java.time.LocalDate;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.statistic.application.event.ParticipationResponses;
import com.example.surveyapi.domain.statistic.application.event.StatisticEventPort;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.participation.ParticipationCreatedGlobalEvent;
import com.example.surveyapi.global.event.survey.SurveyActivateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(queues = RabbitConst.QUEUE_NAME_STATISTIC)
public class StatisticEventConsumer {

	private final StatisticEventPort statisticEventPort;

	@RabbitHandler
	public void consumeParticipationCreatedEvent(ParticipationCreatedGlobalEvent event) {
		try{
			log.info("ParticipationCreatedGlobalEvent received: {}", event);
			ParticipationResponses responses = convertEventToDto(event);
			statisticEventPort.handleParticipationEvent(responses);
		} catch (Exception e) {
			log.error("메시지 처리 중 에러 발생: {}", event, e);
		}
	}

	@RabbitHandler
	public void consumeSurveyActivateEvent(SurveyActivateEvent event) {
		try{
			log.info("get surveyEvent : {}", event);
			log.info("surveyActivateEvent received: {}", event.getSurveyStatus());
			if (event.getSurveyStatus().equals("IN_PROGRESS")) {
				statisticEventPort.handleSurveyActivateEvent(event.getSurveyId());
				return;
			}
			if (event.getSurveyStatus().equals("CLOSED")) {
				statisticEventPort.handleSurveyDeactivateEvent(event.getSurveyId());
			}
		} catch (Exception e) {
			log.error("메시지 처리 중 에러 발생: {}", event, e);
		}
	}

	private ParticipationResponses convertEventToDto(ParticipationCreatedGlobalEvent event) {
		LocalDate localBirth = event.getDemographic().getBirth();
		List<Integer> birth = List.of(localBirth.getYear(), localBirth.getMonthValue(), localBirth.getDayOfMonth());
		String birthDate = formatBirthDate(birth);
		Integer age = calculateAge(birth);
		String ageGroup = calculateAgeGroup(age);

		List<ParticipationResponses.Answer> answers = event.getAnswers().stream()
			.map(answer -> new ParticipationResponses.Answer(
				answer.getQuestionId(), answer.getChoiceIds(), answer.getResponseText()
			)).toList();

		return new ParticipationResponses(
			event.getParticipationId(),
			event.getSurveyId(),
			event.getUserId(),
			event.getDemographic().getGender(),
			birthDate,
			age,
			ageGroup,
			event.getCompletedAt().atZone(java.time.ZoneId.systemDefault()).toInstant(),
			answers
		);
	}

	private String formatBirthDate(List<Integer> birth) {
		if (birth == null || birth.size() < 3) return null;
		return String.format("%d-%02d-%02d", birth.get(0), birth.get(1), birth.get(2));
	}

	private Integer calculateAge(List<Integer> birth) {
		if (birth == null || birth.size() < 1) return null;
		return LocalDate.now().getYear() - birth.get(0);
	}

	private String calculateAgeGroup(Integer age) {
		if (age == null) return "UNKNOWN";
		if (age < 20) return "10s";
		if (age < 30) return "20s";
		if (age < 40) return "30s";
		if (age < 50) return "40s";
		return "50s_OVER";
	}
}
