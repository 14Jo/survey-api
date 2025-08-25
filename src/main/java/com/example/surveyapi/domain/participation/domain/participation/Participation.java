package com.example.surveyapi.domain.participation.domain.participation;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.event.ParticipationCreatedEvent;
import com.example.surveyapi.domain.participation.domain.event.ParticipationUpdatedEvent;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.AbstractRoot;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "participations")
public class Participation extends AbstractRoot<Participation> {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long userId;

	@Column(nullable = false)
	private Long surveyId;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb", nullable = false)
	private ParticipantInfo participantInfo;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb", nullable = false)
	private List<ResponseData> answers = new ArrayList<>();

	public static Participation create(Long userId, Long surveyId, ParticipantInfo participantInfo,
		List<ResponseData> responseDataList) {
		Participation participation = new Participation();
		participation.userId = userId;
		participation.surveyId = surveyId;
		participation.participantInfo = participantInfo;
		participation.answers = responseDataList;

		return participation;
	}

	public void registerCreatedEvent() {
		registerEvent(ParticipationCreatedEvent.from(this));
	}

	public void validateOwner(Long userId) {
		if (!this.userId.equals(userId)) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW);
		}
	}

	public void update(List<ResponseData> responseDataList) {
		this.answers = responseDataList;
		registerEvent(ParticipationUpdatedEvent.from(this));
	}
}
