package com.example.surveyapi.domain.survey.domain.question;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.survey.domain.question.vo.Choice;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionOrderService {

	private final QuestionRepository questionRepository;

	public List<QuestionInfo> adjustDisplayOrder(Long surveyId, List<QuestionInfo> newQuestions) {
		if (newQuestions == null || newQuestions.isEmpty())
			return List.of();

		List<Question> existQuestions = questionRepository.findAllBySurveyId(surveyId);
		existQuestions.sort(Comparator.comparingInt(Question::getDisplayOrder));

		List<QuestionInfo> newQuestionsInfo = new ArrayList<>(newQuestions);
		newQuestionsInfo.sort(Comparator.comparingInt(QuestionInfo::getDisplayOrder));

		List<QuestionInfo> adjustQuestions = new ArrayList<>();

		if (existQuestions.isEmpty()) {
			for (int i = 0; i < newQuestionsInfo.size(); i++) {
				QuestionInfo questionInfo = newQuestionsInfo.get(i);
				adjustQuestions.add(
					new QuestionInfo(
						questionInfo.getContent(), questionInfo.getQuestionType(), questionInfo.isRequired(),
						i + 1, questionInfo.getChoices() == null ? List.of() : questionInfo.getChoices()
					)
				);
			}
			return adjustQuestions;
		}

		for (QuestionInfo newQ : newQuestionsInfo) {
			int insertOrder = newQ.getDisplayOrder();

			for (Question existQ : existQuestions) {
				if (existQ.getDisplayOrder() >= insertOrder) {
					existQ.setDisplayOrder(existQ.getDisplayOrder() + 1);
				}
			}

			adjustQuestions.add(new QuestionInfo(
				newQ.getContent(), newQ.getQuestionType(), newQ.isRequired(), insertOrder,
				newQ.getChoices() == null ? List.of() : newQ.getChoices()
			));
		}

		questionRepository.saveAll(existQuestions);

		return adjustQuestions;
	}
} 