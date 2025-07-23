package com.example.surveyapi.domain.statistic.domain.model.entity;

import com.example.surveyapi.domain.statistic.domain.model.aggregate.Statistics;
import com.example.surveyapi.domain.statistic.domain.model.enums.SourceType;
import com.example.surveyapi.domain.statistic.domain.model.enums.StatisticType;
import com.example.surveyapi.global.model.BaseEntity;

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
import lombok.Getter;

@Getter
@Entity
@Table(name = "statistics_items")
public class StatisticsItem extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	public Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "survey_id")
	private Statistics statistic;

	// private demographicKey = demographicKey;

	//VO 분리여부 검토
	private Long questionId;
	private Long choiceId;
	private int count;
	private float percentage;

	@Enumerated(EnumType.STRING)
	private SourceType source;

	@Enumerated(EnumType.STRING)
	private StatisticType type;

	protected StatisticsItem() {}
}
