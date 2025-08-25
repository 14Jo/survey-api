package com.example.surveyapi.domain.statistic.infra.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.statistic.application.client.SurveyDetailDto;
import com.example.surveyapi.domain.statistic.application.client.SurveyServicePort;
import com.example.surveyapi.global.dto.ExternalApiResponse;
import com.example.surveyapi.global.client.SurveyApiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component("statisticSurveyAdapter")
@RequiredArgsConstructor
public class SurveyServiceAdapter implements SurveyServicePort {

	private final SurveyApiClient surveyApiClient;
	private final ObjectMapper objectMapper;

	@Override
	public SurveyDetailDto getSurveyDetail(String authHeader, Long surveyId) {
		ExternalApiResponse response = surveyApiClient.getSurveyDetail(authHeader, surveyId);
		Object rawData = response.getOrThrow();

		SurveyDetailDto surveyDetail = objectMapper.convertValue(
			rawData,
			new TypeReference<SurveyDetailDto>() {}
		);

		// TODO  choiceId가 생기면 바꾸기
		List<SurveyDetailDto.QuestionInfo> patchedQuestions = surveyDetail.questions().stream()
			.map(question -> {
				List<SurveyDetailDto.ChoiceInfo> patchedChoices = null;

				if (question.choices() != null) {
					patchedChoices = question.choices().stream()
						.map(choice -> {
							if (choice.choiceId() == null) {
								return new SurveyDetailDto.ChoiceInfo(
									(long)choice.displayOrder(),  // displayOrder를 choiceId로
									choice.content(),
									choice.displayOrder()
								);
							} else {
								return choice;
							}
						})
						.toList();
				}

				return new SurveyDetailDto.QuestionInfo(
					question.questionId(),
					question.content(),
					question.questionType(),
					question.displayOrder(),
					patchedChoices
				);

			})
			.toList();

		// 새로운 SurveyDetailDto 반환
		return new SurveyDetailDto(
			surveyDetail.surveyId(),
			surveyDetail.title(),
			patchedQuestions
		);
	}
}
