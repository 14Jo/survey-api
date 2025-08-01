package com.example.surveyapi.domain.statistic.domain.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StatisticCommand {
	List<ParticipationDetailData> participations;

	public record ParticipationDetailData(
		LocalDateTime participatedAt,
		List<ResponseData> responses
	) {}

	public record ResponseData(
		Long questionId,
		Map<String, Object> answer
	) {}
}
