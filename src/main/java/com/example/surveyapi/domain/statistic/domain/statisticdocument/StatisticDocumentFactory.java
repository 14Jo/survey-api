package com.example.surveyapi.domain.statistic.domain.statisticdocument;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.statistic.domain.statisticdocument.dto.DocumentCreateCommand;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.dto.SurveyMetadata;

@Component
public class StatisticDocumentFactory {

	public List<StatisticDocument> initDocuments(Long surveyId, SurveyMetadata metadata) {
		return metadata.getQuestionMap().entrySet().stream()
			.flatMap(data -> createInitialStreamForQuestion(surveyId, data.getKey(), data.getValue()))
			.toList();
	}

	public List<StatisticDocument> createDocuments(DocumentCreateCommand command, SurveyMetadata metadata) {
		return command.answers().stream()
			.flatMap(answer -> createStreamOfDocuments(command, answer, metadata))
			.filter(Objects::nonNull)
			.toList();
	}

	private Stream<StatisticDocument> createInitialStreamForQuestion(Long surveyId, Long questionId, SurveyMetadata.QuestionMetadata questionMeta) {
		if ("SINGLE_CHOICE".equals(questionMeta.questionType()) || "MULTIPLE_CHOICE".equals(questionMeta.questionType())) {
			return questionMeta.choiceMap().entrySet().stream()
				.map(choiceEntry -> {
					Long choiceId = choiceEntry.getKey();
					String choiceText = choiceEntry.getValue();
					return buildInitialDocument(surveyId, questionId, questionMeta, choiceId, choiceText);
				});
		}
		if ("LONG_ANSWER".equals(questionMeta.questionType()) || "SHORT_ANSWER".equals(questionMeta.questionType())) {
			return Stream.of(buildInitialDocument(surveyId, questionId, questionMeta, null, null));
		}
		return Stream.empty();
	}

	private StatisticDocument buildInitialDocument(
		Long surveyId, Long questionId,
		SurveyMetadata.QuestionMetadata questionMeta, Long choiceId, String choiceText
	) {
		String documentId = (choiceId != null) ?
			String.format("%d-%d-%d-init", surveyId, questionId, choiceId) :
			String.format("%d-%d-init", surveyId, questionId);
		String responseText = (choiceId != null) ? null : "";

		return StatisticDocument.create(
			documentId,
			surveyId,
			questionId,
			questionMeta.content(),
			questionMeta.questionType(),
			(choiceId != null) ? choiceId.intValue() : null,
			choiceText,
			responseText, null, null, null, null, null, null
		);
	}

	private Stream<StatisticDocument> createStreamOfDocuments(DocumentCreateCommand command,
		DocumentCreateCommand.Answer answer,
		SurveyMetadata metadata) {
		// 메타데이터에서 현재 응답에 해당하는 질문 정보를 찾는다.
		return metadata.getQuestion(answer.questionId())
			.map(questionMeta -> {
				// 서술형 응답 처리
				if (answer.responseText() != null && !answer.responseText().isEmpty()) {
					return Stream.of(buildDocument(command, answer, questionMeta, null));
				}
				// 선택형 응답 처리 (단일/다중 모두 포함)
				if (answer.choiceIds() != null && !answer.choiceIds().isEmpty()) {
					return answer.choiceIds().stream()
						.map(choiceId -> buildDocument(command, answer, questionMeta, choiceId));
				}
				// 응답 내용이 없는 경우 빈 스트림 반환
				return Stream.<StatisticDocument>empty();
			}).orElse(Stream.empty()); // 질문 정보가 없는 경우 빈 스트림 반환
	}

	private StatisticDocument buildDocument(DocumentCreateCommand command,
		DocumentCreateCommand.Answer answer,
		SurveyMetadata.QuestionMetadata questionMeta,
		Integer choiceId) {

		// 메타데이터에서 선택지 텍스트를 조회
		String choiceText = (choiceId != null) ? questionMeta.getChoiceText(choiceId).orElse(null) : null;

		// 고유한 문서 ID 생성 (서술형은 choiceId가 null)
		String documentId = (choiceId != null) ?
			String.format("%d-%d-%d", command.participationId(), answer.questionId(), choiceId) :
			String.format("%d-%d", command.participationId(), answer.questionId());

		return StatisticDocument.create(
			documentId, command.surveyId(), answer.questionId(), questionMeta.content(),
			questionMeta.questionType(), choiceId, choiceText, answer.responseText(),
			command.userId(), command.userGender(), command.userBirthDate(), command.userAge(),
			command.userAgeGroup(), command.completedAt()
		);
	}
}