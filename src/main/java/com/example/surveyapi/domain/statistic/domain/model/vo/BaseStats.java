package com.example.surveyapi.domain.statistic.domain.model.vo;

import java.time.LocalDateTime;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Getter
@Embeddable
public class BaseStats {
	private int totalResponses;
	private LocalDateTime responseStart;
	private LocalDateTime responseEnd;

	protected BaseStats() {}

	private BaseStats (int totalResponses, LocalDateTime responseStart, LocalDateTime responseEnd) {
		this.totalResponses = totalResponses;
		this.responseStart = responseStart;
		this.responseEnd = responseEnd;
	}

	public static BaseStats of (int totalResponses, LocalDateTime responseStart, LocalDateTime responseEnd) {
		return new BaseStats(totalResponses, responseStart, responseEnd);
	}

	public static BaseStats start(){
		BaseStats baseStats = new BaseStats();
		baseStats.totalResponses = 0;
		baseStats.responseStart = LocalDateTime.now();
		return baseStats;
	}

	public void addTotalResponses (int count) {
		totalResponses += count;
	}

}
