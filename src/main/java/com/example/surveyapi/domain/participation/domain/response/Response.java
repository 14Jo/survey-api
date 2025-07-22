package com.example.surveyapi.domain.participation.domain.response;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.annotations.Type;

import com.example.surveyapi.domain.participation.domain.participation.Participation;
import com.example.surveyapi.domain.participation.domain.response.enums.QuestionType;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "responses")
public class Response {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "participation_id", nullable = false)
	private Participation participation;

	@Column(nullable = false)
	private Long questionId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private QuestionType questionType;

	@Type(JsonType.class)
	@Column(columnDefinition = "jsonb")
	private Map<String, Object> answer = new HashMap<>();

	public static Response create(Long questionId, QuestionType questionType, Map<String, Object> answer) {
		Response response = new Response();
		response.questionId = questionId;
		response.questionType = questionType;
		response.answer = answer;

		return response;
	}
}
