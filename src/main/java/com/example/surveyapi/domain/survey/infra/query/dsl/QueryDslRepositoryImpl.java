package com.example.surveyapi.domain.survey.infra.query.dsl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.query.dto.SurveyDetail;
import com.example.surveyapi.domain.survey.domain.query.dto.SurveyTitle;
import com.example.surveyapi.domain.survey.domain.question.QQuestion;
import com.example.surveyapi.domain.survey.domain.question.Question;
import com.example.surveyapi.domain.survey.domain.survey.QSurvey;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
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

		Survey surveyResult = jpaQueryFactory
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
			.map(q -> QuestionInfo.of(
				q.getContent(),
				q.getType(),
				q.isRequired(),
				q.getDisplayOrder(),
				q.getChoices().stream()
					.map(c -> ChoiceInfo.of(c.getContent(), c.getDisplayOrder()))
					.collect(Collectors.toList())
			))
			.toList();

		SurveyDetail detail = SurveyDetail.of(
			surveyResult,
			questions
		);

		return Optional.of(detail);
	}

	@Override
	public List<SurveyTitle> findSurveyTitlesInCursor(Long projectId, Long lastSurveyId) {
		QSurvey survey = QSurvey.survey;
		int pageSize = 10;

		return jpaQueryFactory
			.select(
				survey.surveyId,
				survey.title,
				survey.status,
				survey.duration
			)
			.from(survey)
			.where(
				survey.projectId.eq(projectId),
				lastSurveyId != null ? survey.surveyId.lt(lastSurveyId) : null
			)
			.orderBy(survey.surveyId.desc())
			.limit(pageSize)
			.fetch()
			.stream()
			.map(tuple -> SurveyTitle.of(
				tuple.get(survey.surveyId),
				tuple.get(survey.title),
				tuple.get(survey.status),
				tuple.get(survey.duration)
			))
			.toList();
	}
}