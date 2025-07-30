package com.example.surveyapi.domain.survey.application.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchSurveyDetailResponse {
	private String title;
	private String description;
	private Duration duration;
	private Option option;
	private Integer participationCount;
	private List<QuestionResponse> questions;



	public static SearchSurveyDetailResponse from(SurveyDetail surveyDetail, Integer count) {
		SearchSurveyDetailResponse response = new SearchSurveyDetailResponse();
		response.title = surveyDetail.getTitle();
		response.description = surveyDetail.getDescription();
		response.duration = Duration.from(surveyDetail.getDuration());
		response.option = Option.from(surveyDetail.getOption());
		response.questions = surveyDetail.getQuestions().stream()
			.map(QuestionResponse::from)
			.toList();
		response.participationCount = count;
		return response;
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Duration {
		private LocalDateTime startDate;
		private LocalDateTime endDate;

		public static Duration from(SurveyDuration duration) {
			Duration result = new Duration();
			result.startDate = duration.getStartDate();
			result.endDate = duration.getEndDate();
			return result;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class Option {
		private boolean anonymous;
		private boolean allowResponseUpdate;

		public static Option from(SurveyOption option) {
			Option result = new Option();
			result.anonymous = option.isAnonymous();
			result.allowResponseUpdate = option.isAllowResponseUpdate();
			return result;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class QuestionResponse {
		private String content;
		private QuestionType questionType;
		private boolean isRequired;
		private int displayOrder;
		private List<ChoiceResponse> choices;

		public static QuestionResponse from(QuestionInfo questionInfo) {
			QuestionResponse result = new QuestionResponse();
			result.content = questionInfo.getContent();
			result.questionType = questionInfo.getQuestionType();
			result.isRequired = questionInfo.isRequired();
			result.displayOrder = questionInfo.getDisplayOrder();
			result.choices = questionInfo.getChoices().stream()
				.map(ChoiceResponse::from)
				.toList();
			return result;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ChoiceResponse {
		private String content;
		private int displayOrder;

		public static ChoiceResponse from(ChoiceInfo choiceInfo) {
			ChoiceResponse result = new ChoiceResponse();
			result.content = choiceInfo.getContent();
			result.displayOrder = choiceInfo.getDisplayOrder();
			return result;
		}
	}
}