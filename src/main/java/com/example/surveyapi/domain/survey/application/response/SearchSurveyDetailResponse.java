package com.example.surveyapi.domain.survey.application.response;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.question.vo.Choice;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchSurveyDetailResponse {
	private Long surveyId;
	private String title;
	private String description;
	private SurveyStatus status;
	private Duration duration;
	private Option option;
	private Integer participationCount;
	private List<QuestionResponse> questions;



	public static SearchSurveyDetailResponse from(SurveyDetail surveyDetail, Integer count) {
		SearchSurveyDetailResponse response = new SearchSurveyDetailResponse();
		response.surveyId = surveyDetail.getSurveyId();
		response.title = surveyDetail.getTitle();
		response.description = surveyDetail.getDescription();
		response.status = surveyDetail.getStatus();
		response.duration = Duration.from(surveyDetail.getDuration());
		response.option = Option.from(surveyDetail.getOption());
		response.questions = surveyDetail.getQuestions().stream()
			.map(QuestionResponse::from)
			.toList();
		response.participationCount = count;
		return response;
	}

	public static SearchSurveyDetailResponse from(SurveyReadEntity entity, Integer participationCount) {
		SearchSurveyDetailResponse response = new SearchSurveyDetailResponse();
		response.surveyId = entity.getSurveyId();
		response.title = entity.getTitle();
		response.description = entity.getDescription();
		response.status = SurveyStatus.valueOf(entity.getStatus());
		response.participationCount = participationCount != null ? participationCount : entity.getParticipationCount();
		
		if (entity.getOptions() != null) {
			response.option = Option.from(entity.getOptions().isAnonymous(), entity.getOptions().isAllowResponseUpdate());
			response.duration = Duration.from(entity.getOptions().getStartDate(), entity.getOptions().getEndDate());
		}
		
		if (entity.getQuestions() != null) {
			response.questions = entity.getQuestions().stream()
				.map(QuestionResponse::from)
				.toList();
		}
		
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

		public static Duration from(LocalDateTime startDate, LocalDateTime endDate) {
			Duration result = new Duration();
			result.startDate = startDate;
			result.endDate = endDate;
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

		public static Option from(boolean anonymous, boolean allowResponseUpdate) {
			Option result = new Option();
			result.anonymous = anonymous;
			result.allowResponseUpdate = allowResponseUpdate;
			return result;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class QuestionResponse {
		private Long questionId;
		private String content;
		private QuestionType questionType;
		private boolean isRequired;
		private int displayOrder;
		private List<ChoiceResponse> choices;

		public static QuestionResponse from(QuestionInfo questionInfo) {
			QuestionResponse result = new QuestionResponse();
			result.questionId = questionInfo.getQuestionId();
			result.content = questionInfo.getContent();
			result.questionType = questionInfo.getQuestionType();
			result.isRequired = questionInfo.isRequired();
			result.displayOrder = questionInfo.getDisplayOrder();
			result.choices = questionInfo.getChoices().stream()
				.map(ChoiceResponse::from)
				.toList();
			return result;
		}

		public static QuestionResponse from(SurveyReadEntity.QuestionSummary questionSummary) {
			QuestionResponse result = new QuestionResponse();
			result.questionId = questionSummary.getQuestionId();
			result.content = questionSummary.getContent();
			result.questionType = questionSummary.getQuestionType();
			result.isRequired = questionSummary.isRequired();
			result.displayOrder = questionSummary.getDisplayOrder();
			result.choices = questionSummary.getChoices().stream()
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

		public static ChoiceResponse from(Choice choice) {
			ChoiceResponse result = new ChoiceResponse();
			result.content = choice.getContent();
			result.displayOrder = choice.getDisplayOrder();
			return result;
		}
	}
}