package com.example.surveyapi.domain.statistic.infra.rabbitmq;

import java.time.LocalDate;
import java.util.List;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.statistic.application.event.ParticipationResponses;
import com.example.surveyapi.domain.statistic.application.event.StatisticEventPort;
import com.example.surveyapi.global.constant.RabbitConst;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor

public class StatisticEventConsumer {

	private final StatisticEventPort statisticEventPort;

	@RabbitListener(queues = RabbitConst.PARTICIPATION_QUEUE_NAME)
	public void consumeParticipationCreatedEvent(ParticipationCreatedEvent event) {
		try{
			ParticipationResponses responses = convertEventToDto(event);
			statisticEventPort.handleParticipationEvent(responses);
		} catch (Exception e) {
			log.error("메시지 처리 중 에러 발생: {}", event, e);
		}
	}

	private ParticipationResponses convertEventToDto(ParticipationCreatedEvent event) {
		List<Integer> birth = event.demographic().birth();
		String birthDate = formatBirthDate(birth);
		Integer age = calculateAge(birth);
		String ageGroup = calculateAgeGroup(age);

		List<ParticipationResponses.Answer> answers = event.answers().stream()
			.map(answer -> new ParticipationResponses.Answer(
				answer.questionId(), answer.choiceIds(), answer.responseText()
			)).toList();

		return new ParticipationResponses(
			event.participationId(),
			event.surveyId(),
			event.userId(),
			event.demographic().gender(),
			birthDate,
			age,
			ageGroup,
			event.completedAt().atZone(java.time.ZoneId.systemDefault()).toInstant(),
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
