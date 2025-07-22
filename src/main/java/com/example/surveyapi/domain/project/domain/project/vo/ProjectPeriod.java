package com.example.surveyapi.domain.project.domain.project.vo;

import java.time.LocalDateTime;

import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectPeriod {

	private LocalDateTime periodStart;
	private LocalDateTime periodEnd;

	public static ProjectPeriod toPeriod(LocalDateTime periodStart, LocalDateTime periodEnd) {
		if (periodEnd != null && periodStart.isAfter(periodEnd)) {
			throw new CustomException(CustomErrorCode.START_DATE_AFTER_END_DATE);
		}
		return new ProjectPeriod(periodStart, periodEnd);
	}
}

