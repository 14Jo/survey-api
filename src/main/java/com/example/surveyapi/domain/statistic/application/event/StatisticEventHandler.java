package com.example.surveyapi.domain.statistic.application.event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.statistic.application.StatisticService;
import com.example.surveyapi.domain.statistic.application.client.dto.SurveyDetailDto;
import com.example.surveyapi.domain.statistic.application.client.SurveyServicePort;
import com.example.surveyapi.domain.statistic.domain.statistic.Statistic;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocument;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocumentFactory;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.StatisticDocumentRepository;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.dto.DocumentCreateCommand;
import com.example.surveyapi.domain.statistic.domain.statisticdocument.dto.SurveyMetadata;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class StatisticEventHandler implements StatisticEventPort {

	private final StatisticService statisticService;
	private final SurveyServicePort surveyServicePort;
	private final StatisticDocumentFactory statisticDocumentFactory;
	private final StatisticDocumentRepository statisticDocumentRepository;

	@Override
	public void handleParticipationEvent(ParticipationResponses responses) {
		Statistic statistic = statisticService.getStatistic(responses.surveyId());
		statistic.verifyIfCounting();

		//TODO : 고치기
		String serviceToken = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlclJvbGUiOiJVU0VSIiwidHlwZSI6ImFjY2VzcyIsImV4cCI6MTc1NTUwNTc2NywiaWF0IjoxNzU1NDg0MTY3fQ.jPpL3Y_jup5GxrzyX92RA_KenRL2QSRms0k_qrggt9Y";

		SurveyDetailDto surveyDetail = surveyServicePort.getSurveyDetail(serviceToken, responses.surveyId());
		SurveyMetadata surveyMetadata = toSurveyMetadata(surveyDetail);

		DocumentCreateCommand command = toCreateCommand(responses);

		//TODO : survey 정보 수정 (캐싱 Or dto 분리)

		List<StatisticDocument> documents = statisticDocumentFactory.createDocuments(command, surveyMetadata);

		if(!documents.isEmpty()) {
			statisticDocumentRepository.saveAll(documents);
		}
	}

	@Override
	public void handleSurveyActivateEvent(Long surveyId) {

	}

	private DocumentCreateCommand toCreateCommand(ParticipationResponses responses) {
		List<DocumentCreateCommand.Answer> answers = responses.answers().stream()
			.map(answer -> new DocumentCreateCommand.Answer(
				answer.questionId(), answer.choiceIds(), answer.responseText()))
			.toList();

		return new DocumentCreateCommand(
			responses.participationId(),
			responses.surveyId(),
			responses.userId(),
			responses.userGender(),
			responses.userBirthDate(),
			responses.userAge(),
			responses.userAgeGroup(),
			responses.completedAt(),
			answers
		);
	}

	private SurveyMetadata toSurveyMetadata(SurveyDetailDto surveyDetailDto) {
		Map<Long, SurveyMetadata.QuestionMetadata> questionMetadataMap = surveyDetailDto.questions().stream()
			.collect(Collectors.toMap(
				SurveyDetailDto.QuestionInfo::questionId,
				questionInfo -> {
					Map<Long, String> choiceMap = (questionInfo.choices() != null) ?
						questionInfo.choices().stream().collect(Collectors.toMap(
							SurveyDetailDto.ChoiceInfo::choiceId,
							SurveyDetailDto.ChoiceInfo::content
						)) : Collections.emptyMap();

					return new SurveyMetadata.QuestionMetadata(
						questionInfo.content(),
						questionInfo.questionType(),
						choiceMap
					);
				}
			));
		return new SurveyMetadata(questionMetadataMap);
	}
}
