package com.example.surveyapi.domain.statistic.domain.model.entity;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistic;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;
import com.example.surveyapi.domain.statistic.domain.model.enums.StatisticType;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "statistics_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StatisticsItem extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_id")
	private Statistic statistic;

	// private demographicKey = demographicKey;

	//VO 분리여부 검토
	private Long questionId;
	private Long choiceId;
	private int count;

	// @Enumerated(EnumType.STRING)
	// private SourceType source;

	@Enumerated(EnumType.STRING)
	private StatisticType type;

	@Enumerated(EnumType.STRING)
	private AnswerType answerType;

	@Column(nullable = false)
	private LocalDateTime statisticHour;

	public static StatisticsItem create(
		Long questionId, Long choiceId, int count,
		StatisticType type, AnswerType answerType,
		LocalDateTime statisticHour
	) {
		StatisticsItem item = new StatisticsItem();
		item.questionId = questionId;
		item.choiceId = choiceId;
		item.count = count;
		item.type = type;
		item.answerType = answerType;
		item.statisticHour = statisticHour;
		return item;
	}

	public void setStatistic(Statistic statistic) {
		this.statistic = statistic;
	}
}
