package com.example.surveyapi.domain.survey.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.QueryService.SurveyReadSyncService;
import com.example.surveyapi.domain.survey.application.QueryService.dto.QuestionSyncDto;
import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.question.QuestionOrderService;
import com.example.surveyapi.domain.survey.domain.question.QuestionRepository;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.global.model.BaseEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionService {

	private final SurveyReadSyncService surveyReadSyncService;
	private final QuestionRepository questionRepository;
	private final QuestionOrderService questionOrderService;

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void create(
		Long surveyId,
		List<QuestionInfo> questions
	) {
		long startTime = System.currentTimeMillis();

		List<Question> questionList = questions.stream().map(question ->
			Question.create(
				surveyId, question.getContent(), question.getQuestionType(),
				question.getDisplayOrder(), question.isRequired(),
				question.getChoices()
					.stream()
					.map(choiceInfo -> Choice.of(choiceInfo.getContent(), choiceInfo.getDisplayOrder()))
					.toList()
			)
		).toList();
		questionRepository.saveAll(questionList);

		try {
			surveyReadSyncService.questionReadSync(
				surveyId,
				questionList.stream().map(QuestionSyncDto::from).toList()
			);
			log.info("질문 생성 후 MongoDB 동기화 요청 완료");
		} catch (Exception e) {
			log.error("질문 생성 후 MongoDB 동기화 요청 실패 {} ", e.getMessage());
		}

		long endTime = System.currentTimeMillis();
		log.info("질문 생성 시간 - 총 {} ms", endTime - startTime);
	}

	@Transactional
	public void delete(Long surveyId) {
		List<Question> questionList = questionRepository.findAllBySurveyId(surveyId);
		questionList.forEach(BaseEntity::delete);
	}

	@Transactional
	public List<QuestionInfo> adjustDisplayOrder(Long surveyId, List<QuestionInfo> newQuestions) {
		return questionOrderService.adjustDisplayOrder(surveyId, newQuestions);
	}
}
