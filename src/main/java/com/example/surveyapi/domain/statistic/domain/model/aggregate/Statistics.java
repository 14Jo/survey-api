package com.example.surveyapi.domain.statistic.domain.model.aggregate;

import java.util.ArrayList;
import java.util.List;

import com.example.surveyapi.domain.statistic.domain.model.enums.StatisticStatus;
import com.example.surveyapi.domain.statistic.domain.model.entity.StatisticsItem;
import com.example.surveyapi.domain.statistic.domain.model.vo.BaseStats;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "statistics")
public class Statistics extends BaseEntity {
	@Id
	private Long surveyId;

	@Enumerated(EnumType.STRING)
	private StatisticStatus status;

	@Embedded
	private BaseStats stats;
	// private int totalResponses;
	// private LocalDateTime responseStart;
	// private LocalDateTime responseEnd;

	@OneToMany(mappedBy = "statistic", cascade = CascadeType.PERSIST)
	private List<StatisticsItem> responses = new ArrayList<>();

	protected Statistics() {}

	public static Statistics create(Long surveyId, StatisticStatus status) {
		Statistics statistic = new Statistics();
		return statistic;
	}
}
