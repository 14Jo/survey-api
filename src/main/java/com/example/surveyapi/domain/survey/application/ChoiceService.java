package com.example.surveyapi.domain.survey.application;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.survey.application.request.CreateChoiceRequest;
import com.example.surveyapi.domain.survey.domain.choice.Choice;
import com.example.surveyapi.domain.survey.domain.choice.ChoiceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChoiceService {

	private final ChoiceRepository choiceRepository;

	//TODO 벌크 인서트 고려
	public void create(Long questionId, List<CreateChoiceRequest> choices) {

		long startTime = System.currentTimeMillis();
		List<Choice> choiceList = choices.stream()
			.map(choice -> Choice.create(questionId, choice.getContent(), choice.getDisplayOrder()))
			.toList();

		choiceRepository.saveAll(choiceList);

		long endTime = System.currentTimeMillis();
		log.info("질문 생성 시간 - 총 {} ms", endTime - startTime);
	}
}
