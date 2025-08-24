package com.example.surveyapi.domain.statistic.domain.statistic;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.statistic.domain.statistic.enums.StatisticStatus;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
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
	private Long finalResponseCount;

	@Enumerated(EnumType.STRING)
	private StatisticStatus status;

	private LocalDateTime startedAt;
	private LocalDateTime endedAt;

	@Version
	private Long version;

	public static Statistic start(Long surveyId) {
		Statistic statistic = new Statistic();
		statistic.surveyId = surveyId;
		statistic.status = StatisticStatus.COUNTING;
		statistic.startedAt = LocalDateTime.now();

		return statistic;
	}

	public void end(long finalCount) {
		if (this.status == StatisticStatus.DONE) {
			return;
		}
		this.status = StatisticStatus.DONE;
		this.finalResponseCount = finalCount;
		this.endedAt = LocalDateTime.now();
	}

	public void verifyIfCounting() {
		if (this.status != StatisticStatus.COUNTING) {
			throw new CustomException(CustomErrorCode.STATISTICS_ALERADY_DONE);
		}
	}
}
