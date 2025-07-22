package com.example.surveyapi.domain.survey.application.request;

import java.util.List;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;

import lombok.Getter;

@Getter
public class CreateQuestionRequest {

	private String content;
	private QuestionType questionType;
	private boolean isRequired;
	private int displayOrder;
	private List<Choice> choices;

}
