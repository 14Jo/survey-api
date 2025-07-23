package com.example.surveyapi.domain.survey.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.domain.question.Question;
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

	private final QuestionRepository questionRepository;

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
					.map(choiceInfo -> new Choice(choiceInfo.getContent(), choiceInfo.getDisplayOrder()))
					.toList()
			)
		).toList();
		questionRepository.saveAll(questionList);
		long endTime = System.currentTimeMillis();
		log.info("질문 생성 시간 - 총 {} ms", endTime - startTime);
	}

	@Transactional
	public void delete(Long surveyId) {
		List<Question> questionList = questionRepository.findAllBySurveyId(surveyId);
		questionList.forEach(BaseEntity::delete);
	}
}
