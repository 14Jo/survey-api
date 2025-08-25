package com.example.surveyapi.domain.survey.infra.query;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.survey.application.qeury.SurveyReadSyncPort;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadRepository;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SurveyDataReconciliation {

	private final SurveyRepository surveyRepository;
	private final SurveyReadRepository surveyReadRepository;
	private final SurveyReadSyncPort surveyReadSync;

	@Scheduled(cron = "0 */10 * * * ?")
	@Transactional(readOnly = true)
	public void reconcileScheduleStates() {
		try {
			log.debug("스케줄 상태 정합성 보정 시작");

			List<SurveyReadEntity> readEntities = surveyReadRepository.findAll();
			if (readEntities.isEmpty()) {
				log.debug("보정할 설문이 없습니다.");
				return;
			}

			int inconsistentCount = 0;
			int correctedCount = 0;

			for (SurveyReadEntity readEntity : readEntities) {
				try {
					var surveyOpt = surveyRepository.findById(readEntity.getSurveyId());
					if (surveyOpt.isEmpty()) {
						log.warn("PostgreSQL에 없는 설문: surveyId={}", readEntity.getSurveyId());
						continue;
					}

					Survey survey = surveyOpt.get();

					// 삭제된 설문 처리
					if (survey.getStatus().name().equals("DELETED")) {
						inconsistentCount++;
						log.warn("삭제된 설문이 MongoDB에 여전히 존재: surveyId={}", survey.getSurveyId());
						
						try {
							surveyReadSync.deleteSurveyRead(survey.getSurveyId());
							correctedCount++;
							log.info("삭제된 설문 MongoDB에서 제거 완료: surveyId={}", survey.getSurveyId());
						} catch (Exception e) {
							log.error("삭제된 설문 제거 실패: surveyId={}, error={}", survey.getSurveyId(), e.getMessage());
						}
					}
					// 일반적인 상태 불일치 처리
					else if (isStateInconsistent(survey, readEntity)) {
						inconsistentCount++;
						log.warn(
							"상태 불일치 발견: surveyId={}, PostgreSQL=[status={}, scheduleState={}], MongoDB=[status={}, scheduleState={}]",
							survey.getSurveyId(),
							survey.getStatus(), survey.getScheduleState(),
							readEntity.getStatus(), readEntity.getScheduleState());

						try {
							surveyReadSync.updateScheduleState(
								survey.getSurveyId(),
								survey.getScheduleState(),
								survey.getStatus()
							);
							correctedCount++;
							log.info("상태 불일치 보정 완료: surveyId={}", survey.getSurveyId());
						} catch (Exception e) {
							log.error("상태 보정 실패: surveyId={}, error={}", survey.getSurveyId(), e.getMessage());
						}
					}
				} catch (Exception e) {
					log.error("설문 상태 검사 중 오류: surveyId={}, error={}", readEntity.getSurveyId(), e.getMessage());
				}
			}

			if (inconsistentCount > 0) {
				log.info("스케줄 상태 정합성 보정 완료: 불일치={}, 보정성공={}",
					inconsistentCount, correctedCount);
			} else {
				log.debug("스케줄 상태 정합성 보정 완료: 모든 데이터 일치");
			}

		} catch (Exception e) {
			log.error("스케줄 상태 정합성 보정 중 오류 발생", e);
		}
	}

	@Scheduled(cron = "0 0 2 * * ?")
	@Transactional(readOnly = true)
	public void generateDataConsistencyReport() {
		try {
			log.info("데이터 정합성 리포트 생성 시작");

			List<SurveyReadEntity> readEntities = surveyReadRepository.findAll();
			if (readEntities.isEmpty()) {
				log.info("MongoDB에 설문 데이터가 없습니다.");
				return;
			}

			int totalMongoSurveys = readEntities.size();
			int orphanedInMongo = 0;
			int validSurveys = 0;

			for (SurveyReadEntity readEntity : readEntities) {
				try {
					var surveyOpt = surveyRepository.findById(readEntity.getSurveyId());
					if (surveyOpt.isEmpty()) {
						orphanedInMongo++;
						log.warn("PostgreSQL에 없는 고아 데이터 발견: surveyId={}, status={}, scheduleState={}",
							readEntity.getSurveyId(), readEntity.getStatus(), readEntity.getScheduleState());
					} else {
						validSurveys++;
					}
				} catch (Exception e) {
					log.error("데이터 정합성 검사 중 오류: surveyId={}, error={}",
						readEntity.getSurveyId(), e.getMessage());
				}
			}

			log.info("=== 데이터 정합성 리포트 ===");
			log.info("MongoDB 총 설문 수: {}", totalMongoSurveys);
			log.info("유효한 설문 수: {}", validSurveys);
			log.info("고아 데이터 수: {}", orphanedInMongo);
			log.info("정합성 비율: {:.2f}%", (double)validSurveys / totalMongoSurveys * 100);

			if (orphanedInMongo > 0) {
				log.error("=== 고아 데이터 발견 - 관리자 확인 필요 ===");
				log.error("총 {} 개의 고아 데이터가 MongoDB에 존재합니다.", orphanedInMongo);
			} else {
				log.info("모든 MongoDB 데이터가 PostgreSQL과 일치합니다.");
			}

		} catch (Exception e) {
			log.error("데이터 정합성 리포트 생성 중 오류 발생", e);
		}
	}

	private boolean isStateInconsistent(Survey survey, SurveyReadEntity readEntity) {
		if (!survey.getScheduleState().name().equals(readEntity.getScheduleState())) {
			return true;
		}

		if (!survey.getStatus().name().equals(readEntity.getStatus())) {
			return true;
		}

		return false;
	}
}