package com.example.surveyapi.domain.participation.domain.participation;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Type;

import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.participation.domain.response.Response;
import com.example.surveyapi.global.model.BaseEntity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "participations")
public class Participation extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Long surveyId;

	@Type(JsonType.class)
	@Column(columnDefinition = "jsonb", nullable = false)
	private ParticipantInfo participantInfo;

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "participation")
	private List<Response> responses = new ArrayList<>();

	public static Participation create(Long memberId, Long surveyId, ParticipantInfo participantInfo) {
		Participation participation = new Participation();
		participation.memberId = memberId;
		participation.surveyId = surveyId;
		participation.participantInfo = participantInfo;

		return participation;
	}

	public void addResponse(Response response) {
		this.responses.add(response);
		response.setParticipation(this);
	}

	// public void delete() {
	// 	this.deletedAt = LocalDateTime.now();
	// }
}
