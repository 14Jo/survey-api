package com.example.surveyapi.domain.participation.domain.participation.query;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ParticipationQueryRepository {

	Page<ParticipationInfo> findparticipationInfos(Long memberId, Pageable pageable);

	Map<Long, Long> countsBySurveyIds(List<Long> surveyIds);

	List<QuestionAnswer> getAnswers(List<Long> questionIds);
}
