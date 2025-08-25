package com.example.surveyapi.domain.project.domain.project.vo;

import java.time.LocalDateTime;

import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class ProjectPeriod {

	private LocalDateTime periodStart;
	private LocalDateTime periodEnd;

	private ProjectPeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {
		if (periodEnd != null && periodStart.isAfter(periodEnd)) {
			throw new CustomException(CustomErrorCode.START_DATE_AFTER_END_DATE);
		}
		this.periodStart = periodStart;
		this.periodEnd = periodEnd;
	}

	public static ProjectPeriod of(LocalDateTime periodStart, LocalDateTime periodEnd) {
		return new ProjectPeriod(periodStart, periodEnd);
	}
}

