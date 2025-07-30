package com.example.surveyapi.domain.statistic.domain.model.aggregate;

import java.util.ArrayList;
import java.util.List;

import com.example.surveyapi.domain.statistic.domain.model.entity.StatisticsItem;
import com.example.surveyapi.domain.statistic.domain.model.enums.StatisticStatus;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "statistics")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Statistic extends BaseEntity {
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

	public static Statistic create(Long surveyId) {
		Statistic statistic = new Statistic();
		statistic.surveyId = surveyId;
		statistic.status = StatisticStatus.COUNTING;
		statistic.stats = BaseStats.start();
		return statistic;
	}
}
