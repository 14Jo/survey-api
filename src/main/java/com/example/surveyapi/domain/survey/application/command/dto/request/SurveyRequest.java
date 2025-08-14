package com.example.surveyapi.domain.survey.application.command.dto.request;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public abstract class SurveyRequest {

	protected String title;
	protected String description;
	protected SurveyType surveyType;
	protected Duration surveyDuration;
	protected Option surveyOption;

	@Valid
	protected List<QuestionRequest> questions;

	@AssertTrue(message = "시작 일과 종료를 입력 해야 합니다.")
	public boolean isValidDuration() {
		return surveyDuration != null && surveyDuration.getStartDate() != null && surveyDuration.getEndDate() != null;
	}

	@AssertTrue(message = "시작 일은 종료 일보다 이전 이어야 합니다.")
	public boolean isStartBeforeEnd() {
		return isValidDuration() && surveyDuration.getStartDate().isBefore(surveyDuration.getEndDate());
	}

	@AssertTrue(message = "종료 일은 현재 보다 이후 여야 합니다.")
	public boolean isEndAfterNow() {
		return isValidDuration() && surveyDuration.getEndDate().isAfter(LocalDateTime.now());
	}

	@AssertTrue(message = "시작 일은 현재 보다 이후 여야 합니다.")
	public boolean isStartAfterNow() {
		return isValidDuration() && surveyDuration.getStartDate().isAfter(LocalDateTime.now());
	}

	@Getter
	public static class Duration {
		private LocalDateTime startDate;
		private LocalDateTime endDate;

		public SurveyDuration toSurveyDuration() {
			return SurveyDuration.of(startDate, endDate);
		}
	}

	@Getter
	public static class Option {
		private Boolean anonymous = false;
		private Boolean allowResponseUpdate = false;

		public SurveyOption toSurveyOption() {
			return SurveyOption.of(anonymous, allowResponseUpdate);
		}
	}

	@Getter
	public static class QuestionRequest {
		@NotBlank(message = "질문 내용은 필수입니다.")
		private String content;

		@NotNull(message = "질문 타입은 필수입니다.")
		private QuestionType questionType;

		@NotNull(message = "수정 허용 여부는 필수 입니다.")
		private Boolean isRequired;

		@NotNull(message = "표시 순서는 필수입니다.")
		private Integer displayOrder;

		private List<ChoiceRequest> choices;

		@AssertTrue(message = "다중 선택지 문항에 선택지가 없습니다.")
		public boolean isValid() {
			if (questionType == QuestionType.MULTIPLE_CHOICE) {
				return choices != null && choices.size() > 1;
			}
			return true;
		}

		@Getter
		public static class ChoiceRequest {
			@NotBlank(message = "선택지 내용은 필수입니다.")
			private String content;

			@NotNull(message = "표시 순서는 필수입니다.")
			private Integer choiceId;

			public ChoiceInfo toChoiceInfo() {
				return ChoiceInfo.of(content, choiceId);
			}
		}

		public QuestionInfo toQuestionInfo() {
			return QuestionInfo.of(
				content,
				questionType,
				isRequired,
				displayOrder,
				choices != null ? choices.stream().map(ChoiceRequest::toChoiceInfo).toList() : List.of()
			);
		}
	}
} 