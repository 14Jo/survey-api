package com.example.surveyapi.survey.domain.survey.event;

import java.util.List;

import com.example.surveyapi.survey.domain.question.Question;
import com.example.surveyapi.survey.domain.survey.Survey;
import com.example.surveyapi.survey.domain.survey.enums.ScheduleState;
import com.example.surveyapi.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.survey.domain.survey.vo.SurveyOption;

import lombok.Getter;

@Getter
public class UpdatedEvent {

	Survey survey;

	public UpdatedEvent(Survey survey) {
		this.survey = survey;
	}

	public Long getSurveyId() {
		return survey.getSurveyId();
	}

	public Long getProjectId() {
		return survey.getProjectId();
	}

	public Long getCreatorId() {
		return survey.getCreatorId();
	}

	public String getTitle() {
		return survey.getTitle();
	}

	public String getDescription() {
		return survey.getDescription();
	}

	public SurveyStatus getStatus() {
		return survey.getStatus();
	}

	public ScheduleState getScheduleState() {
		return survey.getScheduleState();
	}

	public SurveyOption getOption() {
		return survey.getOption();
	}

	public SurveyDuration getDuration() {
		return survey.getDuration();
	}

	public List<Question> getQuestions() {
		return survey.getQuestions();
	}
}