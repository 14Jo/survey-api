package com.example.surveyapi.domain.survey.infra.query.dsl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.question.QQuestion;
import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.survey.QSurvey;
import com.example.surveyapi.domain.survey.domain.survey.vo.ChoiceInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class QueryDslRepositoryImpl implements QueryDslRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<SurveyDetail> findSurveyDetailBySurveyId(Long surveyId) {
		QSurvey survey = QSurvey.survey;
		QQuestion question = QQuestion.question;

		var surveyResult = jpaQueryFactory
			.selectFrom(survey)
			.where(survey.surveyId.eq(surveyId))
			.fetchOne();

		if (surveyResult == null) {
			return Optional.empty();
		}

		List<Question> questionEntities = jpaQueryFactory
			.selectFrom(question)
			.where(question.surveyId.eq(surveyId))
			.fetch();

		List<QuestionInfo> questions = questionEntities.stream()
			.map(q -> new QuestionInfo(
				q.getContent(),
				q.getType(),
				q.isRequired(),
				q.getDisplayOrder(),
				q.getChoices().stream()
					.map(c -> new ChoiceInfo(c.getContent(), c.getDisplayOrder()))
					.collect(Collectors.toList())
			))
			.toList();

		SurveyDetail detail = new SurveyDetail(
			surveyResult.getTitle(),
			surveyResult.getDescription(),
			surveyResult.getDuration(),
			surveyResult.getOption(),
			questions
		);

		return Optional.of(detail);
	}
}