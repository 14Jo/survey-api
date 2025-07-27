package com.example.surveyapi.domain.participation.domain.participation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.example.surveyapi.domain.participation.domain.command.ResponseData;
import com.example.surveyapi.domain.participation.domain.participation.vo.ParticipantInfo;
import com.example.surveyapi.domain.participation.domain.response.Response;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.example.surveyapi.global.model.BaseEntity;

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

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb", nullable = false)
	private ParticipantInfo participantInfo;

	@OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, mappedBy = "participation")
	private List<Response> responses = new ArrayList<>();

	public static Participation create(Long memberId, Long surveyId, ParticipantInfo participantInfo,
		List<ResponseData> responseDataList) {
		Participation participation = new Participation();
		participation.memberId = memberId;
		participation.surveyId = surveyId;
		participation.participantInfo = participantInfo;
		participation.addResponse(responseDataList);

		return participation;
	}

	private void addResponse(List<ResponseData> responseDataList) {
		for (ResponseData responseData : responseDataList) {
			// TODO: questionId가 해당 survey에 속하는지(보류), 받아온 questionType으로 answer의 key값이 올바른지 유효성 검증
			Response response = Response.create(responseData.getQuestionId(), responseData.getAnswer());

			this.responses.add(response);
			response.setParticipation(this);
		}
	}

	public void validateOwner(Long memberId) {
		if (!this.memberId.equals(memberId)) {
			throw new CustomException(CustomErrorCode.ACCESS_DENIED_PARTICIPATION_VIEW);
		}
	}

	public void update(List<Response> newResponses) {
		Map<Long, Response> responseMap = this.responses.stream()
			.collect(Collectors.toMap(Response::getQuestionId, response -> response));

		// TODO: 고려할 점 - 설문이 수정되고 문항수가 늘어나거나 적어진다면? 문항의 타입 또는 필수 답변 여부가 달라진다면?
		for (Response newResponse : newResponses) {
			Response response = responseMap.get(newResponse.getQuestionId());

			if (response != null) {
				response.updateAnswer(newResponse.getAnswer());
			}
		}
	}
}
