package com.example.surveyapi.statistic.domain.statisticdocument;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(indexName = "statistics")
@Setting(settingPath = "elasticsearch/statistic-settings.json")
@Mapping(mappingPath = "elasticsearch/statistic-mappings.json")
@NoArgsConstructor
public class StatisticDocument {

	@Id
	private String responseId;

	private Long surveyId;

	private Long questionId;
	private String questionText;
	private String questionType;

	private Integer choiceId;
	private String choiceText;
	private String responseText;

	private Long userId;
	private String userGender;
	private String userBirthDate;
	private Integer userAge;
	private String userAgeGroup;

	private Instant submittedAt;

	private StatisticDocument(
		String responseId, Long surveyId, Long questionId, String questionText, String questionType,
		Integer choiceId, String choiceText, String responseText, Long userId, String userGender, String userBirthDate,
		Integer userAge, String userAgeGroup, Instant submittedAt
	) {
		this.responseId = responseId;
		this.surveyId = surveyId;
		this.questionId = questionId;
		this.questionText = questionText;
		this.questionType = questionType;
		this.choiceId = choiceId;
		this.choiceText = choiceText;
		this.responseText = responseText;
		this.userId = userId;
		this.userGender = userGender;
		this.userBirthDate = userBirthDate;
		this.userAge = userAge;
		this.userAgeGroup = userAgeGroup;
		this.submittedAt = submittedAt;
	}

	public static StatisticDocument create(
		String responseId, Long surveyId, Long questionId, String questionText,
		String questionType, Integer choiceId, String choiceText, String responseText,
		Long userId, String userGender, String userBirthDate, Integer userAge,
		String userAgeGroup, Instant submittedAt) {

		return new StatisticDocument(
			responseId, surveyId, questionId, questionText, questionType,
			choiceId, choiceText, responseText, userId, userGender,
			userBirthDate, userAge, userAgeGroup, submittedAt
		);
	}
}
