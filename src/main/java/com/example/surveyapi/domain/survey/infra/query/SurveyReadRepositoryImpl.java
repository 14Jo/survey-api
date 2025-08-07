package com.example.surveyapi.domain.survey.infra.query;

import static org.springframework.data.domain.Sort.*;
import static org.springframework.data.domain.Sort.Direction.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SurveyReadRepositoryImpl implements SurveyReadRepository {

	private final MongoTemplate mongoTemplate;

	@Override
	public List<SurveyReadEntity> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable) {
		Query query = new Query(Criteria.where("projectId").is(projectId));
		query.with(by(DESC, "createdAt"));
		query.limit(pageable.getPageSize());
		return mongoTemplate.find(query, SurveyReadEntity.class);
	}

	@Override
	public List<SurveyReadEntity> findByProjectIdAndSurveyIdGreaterThanOrderByCreatedAtDesc(
		Long projectId, Long lastSurveyId, Pageable pageable
	) {
		Query query = new Query(Criteria.where("projectId").is(projectId));
		query.addCriteria(Criteria.where("surveyId").gt(lastSurveyId));
		query.with(by(DESC, "createdAt"));
		query.limit(pageable.getPageSize());
		return mongoTemplate.find(query, SurveyReadEntity.class);
	}

	@Override
	public List<SurveyReadEntity> findAll() {
		return mongoTemplate.findAll(SurveyReadEntity.class);
	}

	@Override
	public Optional<SurveyReadEntity> findBySurveyId(Long surveyId) {
		Query query = new Query(Criteria.where("surveyId").is(surveyId));
		return Optional.ofNullable(mongoTemplate.findOne(query, SurveyReadEntity.class));
	}

	@Override
	public List<SurveyReadEntity> findBySurveyIdIn(List<Long> surveyIds) {
		Query query = new Query(Criteria.where("surveyId").in(surveyIds));
		return mongoTemplate.find(query, SurveyReadEntity.class);
	}

	@Override
	public List<SurveyReadEntity> findByStatus(String status) {
		Query query = new Query(Criteria.where("status").is(status));
		return mongoTemplate.find(query, SurveyReadEntity.class);
	}

	@Override
	public SurveyReadEntity save(SurveyReadEntity surveyRead) {
		return mongoTemplate.save(surveyRead);
	}

	@Override
	public void saveAll(List<SurveyReadEntity> surveyReads) {
		if (surveyReads.isEmpty()) {
			return;
		}

		BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, SurveyReadEntity.class);

		for (SurveyReadEntity surveyRead : surveyReads) {
			Query query = new Query(Criteria.where("surveyId").is(surveyRead.getSurveyId()));
			Update update = new Update()
				.set("title", surveyRead.getTitle())
				.set("description", surveyRead.getDescription())
				.set("status", surveyRead.getStatus())
				.set("participationCount", surveyRead.getParticipationCount())
				.set("options", surveyRead.getOptions())
				.set("questions", surveyRead.getQuestions());

			bulkOps.upsert(query, update);
		}

		bulkOps.execute();
	}

	@Override
	public void deleteBySurveyId(Long surveyId) {
		Query query = new Query(Criteria.where("surveyId").is(surveyId));
		mongoTemplate.remove(query, SurveyReadEntity.class);
	}

	@Override
	public void updateStatusBySurveyId(Long surveyId, String status) {
		Query query = new Query(Criteria.where("surveyId").is(surveyId));
		Update update = new Update().set("status", status);
		mongoTemplate.updateFirst(query, update, SurveyReadEntity.class);
	}

	@Override
	public void updateBySurveyId(SurveyReadEntity surveyRead) {
		Query query = new Query(Criteria.where("surveyId").is(surveyRead.getSurveyId()));
		Update update = new Update()
			.set("title", surveyRead.getTitle())
			.set("description", surveyRead.getDescription())
			.set("status", surveyRead.getStatus())
			.set("options", surveyRead.getOptions())
			.set("questions", surveyRead.getQuestions());
		mongoTemplate.updateFirst(query, update, SurveyReadEntity.class);
	}
}
