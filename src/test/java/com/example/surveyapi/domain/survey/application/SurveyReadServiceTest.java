package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.qeury.SurveyReadService;
import com.example.surveyapi.domain.survey.application.command.dto.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.command.dto.response.SearchSurveyStatusResponse;
import com.example.surveyapi.domain.survey.application.command.dto.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadRepository;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Testcontainers
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SurveyReadServiceTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
        .withDatabaseName("testdb")
        .withUsername("test")
        .withPassword("test");

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7")
        .withReuse(true)
        .withStartupTimeout(Duration.ofMinutes(2));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test_survey_read_db");
    }

    @Autowired
    private SurveyReadService surveyReadService;

    @Autowired
    private SurveyReadRepository surveyReadRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        // MongoDB 컬렉션 초기화
        mongoTemplate.dropCollection(SurveyReadEntity.class);
    }

    @Test
    @DisplayName("설문 상세 조회 - 성공")
    void findSurveyDetailById_success() {
        // given
        Survey testSurvey = createTestSurvey(1L, "상세 조회용 설문");
        
        // MongoDB에 동기화 데이터 생성
        SurveyReadEntity surveyReadEntity = createTestSurveyReadEntity(testSurvey);
        surveyReadRepository.save(surveyReadEntity);

        // when
        SearchSurveyDetailResponse detail = surveyReadService.findSurveyDetailById(testSurvey.getSurveyId());

        // then
        assertThat(detail).isNotNull();
        assertThat(detail.getSurveyId()).isEqualTo(testSurvey.getSurveyId());
        assertThat(detail.getTitle()).isEqualTo(testSurvey.getTitle());
        assertThat(detail.getDescription()).isEqualTo(testSurvey.getDescription());
    }

    @Test
    @DisplayName("설문 상세 조회 - 존재하지 않는 설문")
    void findSurveyDetailById_notFound() {
        // given
        Long nonExistentId = -1L;

        // when & then
        assertThatThrownBy(() -> surveyReadService.findSurveyDetailById(nonExistentId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 - 성공")
    void findSurveyByProjectId_success() {
        // given
        Long projectId = 1L;
        Survey survey1 = createTestSurvey(projectId, "프로젝트 1의 설문 1");
        Survey survey2 = createTestSurvey(projectId, "프로젝트 1의 설문 2");
        Survey otherProjectSurvey = createTestSurvey(2L, "다른 프로젝트 설문");

        // MongoDB에 동기화 데이터 생성
        surveyReadRepository.save(createTestSurveyReadEntity(survey1));
        surveyReadRepository.save(createTestSurveyReadEntity(survey2));

        // when
        List<SearchSurveyTitleResponse> list = surveyReadService.findSurveyByProjectId(projectId, null);

        // then
        assertThat(list).hasSize(2);
        assertThat(list).extracting("title")
            .containsExactlyInAnyOrder("프로젝트 1의 설문 1", "프로젝트 1의 설문 2");
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 - 커서 기반 페이징 성공")
    void findSurveyByProjectId_with_cursor_success() {
        // given
        Long projectId = 1L;
        Survey survey1 = createTestSurvey(projectId, "프로젝트 1의 설문 1");
        Survey survey2 = createTestSurvey(projectId, "프로젝트 1의 설문 2");
        Survey survey3 = createTestSurvey(projectId, "프로젝트 1의 설문 3");

        // MongoDB에 동기화 데이터 생성 (surveyId를 명시적으로 설정)
        surveyReadRepository.save(createTestSurveyReadEntityWithId(survey1, 1L));
        surveyReadRepository.save(createTestSurveyReadEntityWithId(survey2, 2L));
        surveyReadRepository.save(createTestSurveyReadEntityWithId(survey3, 3L));

        // when - survey2의 ID를 커서로 사용하여 그 이전 설문들을 조회
        List<SearchSurveyTitleResponse> list = surveyReadService.findSurveyByProjectId(projectId, 2L);

        // 디버깅을 위한 출력
        System.out.println("조회된 설문 개수: " + list.size());
        list.forEach(survey -> System.out.println("설문 ID: " + survey.getSurveyId() + ", 제목: " + survey.getTitle()));

        // then - survey2보다 surveyId가 큰 설문들만 조회되어야 함 (내림차순 정렬)
        assertThat(list).hasSize(1);
        // surveyId가 큰 값이 먼저 나오므로 survey3이 조회되어야 함
        assertThat(list.get(0).getTitle()).isEqualTo("프로젝트 1의 설문 3");
    }

    @Test
    @DisplayName("설문 목록 조회 - ID 리스트로 조회 성공")
    void findSurveys_success() {
        // given
        Survey survey1 = createTestSurvey(1L, "ID 리스트 조회 1");
        Survey survey2 = createTestSurvey(1L, "ID 리스트 조회 2");
        List<Long> surveyIdsToFind = List.of(1L, 2L);

        // MongoDB에 동기화 데이터 생성 (surveyId를 명시적으로 설정)
        surveyReadRepository.save(createTestSurveyReadEntityWithId(survey1, 1L));
        surveyReadRepository.save(createTestSurveyReadEntityWithId(survey2, 2L));

        // when
        List<SearchSurveyTitleResponse> list = surveyReadService.findSurveys(surveyIdsToFind);

        // then
        assertThat(list).hasSize(2);
        assertThat(list).extracting("title")
            .containsExactlyInAnyOrder("ID 리스트 조회 1", "ID 리스트 조회 2");
    }

    @Test
    @DisplayName("설문 상태별 조회 - 성공")
    void findBySurveyStatus_success() {
        // given
        Survey preparingSurvey = createTestSurvey(1L, "준비중 설문");

        Survey inProgressSurvey = createTestSurvey(1L, "진행중 설문");
        inProgressSurvey.open();

        // MongoDB에 동기화 데이터 생성
        surveyReadRepository.save(createTestSurveyReadEntity(preparingSurvey));
        surveyReadRepository.save(createTestSurveyReadEntity(inProgressSurvey));

        // when
        SearchSurveyStatusResponse response = surveyReadService.findBySurveyStatus("PREPARING");

        // then
        assertThat(response).isNotNull();
        assertThat(response.getSurveyIds()).hasSize(1);
        assertThat(response.getSurveyIds()).contains(preparingSurvey.getSurveyId());
    }

    @Test
    @DisplayName("설문 상태별 조회 - 잘못된 상태값")
    void findBySurveyStatus_invalidStatus() {
        // given
        String invalidStatus = "INVALID_STATUS";

        // when & then
        assertThatThrownBy(() -> surveyReadService.findBySurveyStatus(invalidStatus))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.STATUS_INVALID_FORMAT);
    }

    private Survey createTestSurvey(Long projectId, String title) {
        return Survey.create(
            projectId,
            1L,
            title,
            "description",
            SurveyType.SURVEY,
            SurveyDuration.of(LocalDateTime.now(), LocalDateTime.now().plusDays(5)),
            SurveyOption.of(false, false),
            List.of()
        );
    }

    private SurveyReadEntity createTestSurveyReadEntity(Survey survey) {
        SurveyReadEntity.SurveyOptions options = new SurveyReadEntity.SurveyOptions(
            survey.getOption().isAnonymous(),
            survey.getOption().isAllowResponseUpdate(),
            survey.getDuration().getStartDate(),
            survey.getDuration().getEndDate()
        );

        return SurveyReadEntity.create(
            survey.getSurveyId(),
            survey.getProjectId(),
            survey.getTitle(),
            survey.getDescription(),
            survey.getStatus().name(),
            0,
            options
        );
    }

    private SurveyReadEntity createTestSurveyReadEntityWithId(Survey survey, Long surveyId) {
        SurveyReadEntity.SurveyOptions options = new SurveyReadEntity.SurveyOptions(
            survey.getOption().isAnonymous(),
            survey.getOption().isAllowResponseUpdate(),
            survey.getDuration().getStartDate(),
            survey.getDuration().getEndDate()
        );

        return SurveyReadEntity.create(
            surveyId,
            survey.getProjectId(),
            survey.getTitle(),
            survey.getDescription(),
            survey.getStatus().name(),
            0,
            options
        );
    }
}