package com.example.surveyapi.domain.statistic.domain.model.aggregate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.surveyapi.domain.statistic.domain.dto.StatisticCommand;
import com.example.surveyapi.domain.statistic.domain.model.entity.StatisticsItem;
import com.example.surveyapi.domain.statistic.domain.model.enums.AnswerType;
import com.example.surveyapi.domain.statistic.domain.model.enums.StatisticStatus;
import com.example.surveyapi.domain.statistic.domain.model.enums.StatisticType;
import com.example.surveyapi.domain.statistic.domain.model.response.Response;
import com.example.surveyapi.domain.statistic.domain.model.response.ResponseFactory;
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

	@OneToMany(mappedBy = "statistic", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	private List<StatisticsItem> responses = new ArrayList<>();

	public record ChoiceIdentifier(Long qId, Long cId, AnswerType type) {}

	public static Statistic create(Long surveyId) {
		Statistic statistic = new Statistic();
		statistic.surveyId = surveyId;
		statistic.status = StatisticStatus.COUNTING;
		statistic.stats = BaseStats.start();
		return statistic;
	}

	public void calculate(StatisticCommand command) {
		this.stats.addTotalResponses(command.getParticipations().size());

		Map<ChoiceIdentifier, Long> counts = command.getParticipations().stream()
			.flatMap(data -> data.responses().stream())
			.map(ResponseFactory::createFrom)
			.flatMap(Response::getIdentifiers)
			.collect(Collectors.groupingBy(
				id -> id,
				Collectors.counting()
			));

		List<StatisticsItem> newItems = counts.entrySet().stream()
			.map(entry -> {
				ChoiceIdentifier id = entry.getKey();
				int count = entry.getValue().intValue();

				return StatisticsItem.create(id.qId, id.cId, count,
					decideType(), id.type);
			}).toList();

		newItems.forEach(item -> item.setStatistic(this));
		this.responses.addAll(newItems);
	}

	private StatisticType decideType() {
		if(status == StatisticStatus.COUNTING) {
			return StatisticType.LIVE;
		}
		return StatisticType.BASE;
	}
}
