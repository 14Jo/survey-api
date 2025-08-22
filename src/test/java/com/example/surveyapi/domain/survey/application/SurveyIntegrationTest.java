package com.example.surveyapi.domain.survey.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.domain.survey.application.command.SurveyService;
import com.example.surveyapi.domain.survey.application.command.dto.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.command.dto.request.SurveyRequest;
import com.example.surveyapi.domain.survey.application.command.dto.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.application.command.dto.response.SearchSurveyDetailResponse;
import com.example.surveyapi.domain.survey.application.command.dto.response.SearchSurveyTitleResponse;
import com.example.surveyapi.domain.survey.application.qeury.SurveyReadService;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadEntity;
import com.example.surveyapi.domain.survey.domain.query.SurveyReadRepository;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@DisplayName("설문 서비스 통합 테스트")
class SurveyIntegrationTest extends IntegrationTestBase {

    @Autowired
    private SurveyService surveyService;

    @Autowired
    private SurveyReadService surveyReadService;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private SurveyReadRepository surveyReadRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockitoBean
    private ProjectPort projectPort;

    private CreateSurveyRequest createRequest;
    private UpdateSurveyRequest updateRequest;
    private final String authHeader = "Bearer test-token";
    private final Long creatorId = 1L;
    private final Long projectId = 1L;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(SurveyReadEntity.class);
        // Mock 설정
        ProjectValidDto validProject = ProjectValidDto.of(List.of(creatorId.intValue()), projectId);
        ProjectStateDto openProjectState = ProjectStateDto.of("IN_PROGRESS");
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
        when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(openProjectState);

        // CreateSurveyRequest 설정
        createRequest = new CreateSurveyRequest();
        ReflectionTestUtils.setField(createRequest, "title", "통합 테스트 설문");
        ReflectionTestUtils.setField(createRequest, "description", "통합 테스트용 설문 설명");
        ReflectionTestUtils.setField(createRequest, "surveyType", SurveyType.VOTE);

        // Duration 설정
        SurveyRequest.Duration duration = new SurveyRequest.Duration();
        ReflectionTestUtils.setField(duration, "startDate", LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(7));
        ReflectionTestUtils.setField(createRequest, "surveyDuration", duration);

        // Option 설정
        SurveyRequest.Option option = new SurveyRequest.Option();
        ReflectionTestUtils.setField(option, "anonymous", true);
        ReflectionTestUtils.setField(option, "allowResponseUpdate", true);
        ReflectionTestUtils.setField(createRequest, "surveyOption", option);

        // Question 설정
        SurveyRequest.QuestionRequest questionRequest = new SurveyRequest.QuestionRequest();
        ReflectionTestUtils.setField(questionRequest, "content", "좋아하는 색깔은?");
        ReflectionTestUtils.setField(questionRequest, "questionType", QuestionType.SINGLE_CHOICE);
        ReflectionTestUtils.setField(questionRequest, "isRequired", true);
        ReflectionTestUtils.setField(questionRequest, "displayOrder", 1);

        // Choice 설정
        SurveyRequest.QuestionRequest.ChoiceRequest choice1 = new SurveyRequest.QuestionRequest.ChoiceRequest();
        ReflectionTestUtils.setField(choice1, "content", "빨강");
        ReflectionTestUtils.setField(choice1, "choiceId", 1);

        SurveyRequest.QuestionRequest.ChoiceRequest choice2 = new SurveyRequest.QuestionRequest.ChoiceRequest();
        ReflectionTestUtils.setField(choice2, "content", "파랑");
        ReflectionTestUtils.setField(choice2, "choiceId", 2);

        ReflectionTestUtils.setField(questionRequest, "choices", List.of(choice1, choice2));
        ReflectionTestUtils.setField(createRequest, "questions", List.of(questionRequest));

        // UpdateSurveyRequest 설정
        updateRequest = new UpdateSurveyRequest();
        ReflectionTestUtils.setField(updateRequest, "title", "수정된 설문 제목");
        ReflectionTestUtils.setField(updateRequest, "description", "수정된 설문 설명");
    }

    @Test
    @DisplayName("설문 생성 후 조회 테스트 - CQRS 패턴 검증")
    void createSurveyAndQueryTest() {
        // given & when
        Long surveyId = surveyService.create(authHeader, projectId, creatorId, createRequest);

        // then
        Optional<Survey> savedSurvey = surveyRepository.findById(surveyId);
        assertThat(savedSurvey).isPresent();
        assertThat(savedSurvey.get().getTitle()).isEqualTo("통합 테스트 설문");
        assertThat(savedSurvey.get().getCreatorId()).isEqualTo(creatorId);
        assertThat(savedSurvey.get().getProjectId()).isEqualTo(projectId);

        Optional<SurveyReadEntity> readEntity = surveyReadRepository.findBySurveyId(surveyId);
        assertThat(readEntity).isPresent();
        assertThat(readEntity.get().getTitle()).isEqualTo("통합 테스트 설문");

        SearchSurveyDetailResponse detailResponse = surveyReadService.findSurveyDetailById(surveyId);
        assertThat(detailResponse.getTitle()).isEqualTo("통합 테스트 설문");
        assertThat(detailResponse.getDescription()).isEqualTo("통합 테스트용 설문 설명");
        assertThat(detailResponse.getStatus()).isEqualTo(SurveyStatus.PREPARING);
    }

    @Test
    @DisplayName("프로젝트별 설문 목록 조회 테스트")
    void findSurveysByProjectIdTest() {
        // given
        Long surveyId1 = surveyService.create(authHeader, projectId, creatorId, createRequest);

        ReflectionTestUtils.setField(createRequest, "title", "두 번째 설문");
        Long surveyId2 = surveyService.create(authHeader, projectId, creatorId, createRequest);

        // when
        List<SearchSurveyTitleResponse> surveys = surveyReadService.findSurveyByProjectId(projectId, null);

        // then
        assertThat(surveys).hasSize(2);
        assertThat(surveys)
            .extracting(SearchSurveyTitleResponse::getTitle)
            .containsExactlyInAnyOrder("통합 테스트 설문", "두 번째 설문");
    }

    @Test
    @DisplayName("설문 수정 후 조회 테스트")
    void updateSurveyAndQueryTest() {
        // given
        Long surveyId = surveyService.create(authHeader, projectId, creatorId, createRequest);

        // when
        surveyService.update(authHeader, surveyId, creatorId, updateRequest);

        // then
        Optional<Survey> updatedSurvey = surveyRepository.findById(surveyId);
        assertThat(updatedSurvey).isPresent();
        assertThat(updatedSurvey.get().getTitle()).isEqualTo("수정된 설문 제목");
        assertThat(updatedSurvey.get().getDescription()).isEqualTo("수정된 설문 설명");

        SearchSurveyDetailResponse detailResponse = surveyReadService.findSurveyDetailById(surveyId);
        assertThat(detailResponse.getTitle()).isEqualTo("수정된 설문 제목");
        assertThat(detailResponse.getDescription()).isEqualTo("수정된 설문 설명");
    }

    @Test
    @DisplayName("설문 상태 변경 후 조회 테스트")
    void surveyStatusChangeAndQueryTest() {
        // given
        Long surveyId = surveyService.create(authHeader, projectId, creatorId, createRequest);

        // when
        surveyService.open(authHeader, surveyId, creatorId);

        // then
        Optional<Survey> survey = surveyRepository.findById(surveyId);
        assertThat(survey).isPresent();
        assertThat(survey.get().getStatus()).isEqualTo(SurveyStatus.IN_PROGRESS);

        SearchSurveyDetailResponse detailResponse = surveyReadService.findSurveyDetailById(surveyId);
        assertThat(detailResponse.getStatus()).isEqualTo(SurveyStatus.IN_PROGRESS);

        // when
        surveyService.close(authHeader, surveyId, creatorId);

        // then
        Optional<Survey> closedSurvey = surveyRepository.findById(surveyId);
        assertThat(closedSurvey).isPresent();
        assertThat(closedSurvey.get().getStatus()).isEqualTo(SurveyStatus.CLOSED);

        SearchSurveyDetailResponse closedDetailResponse = surveyReadService.findSurveyDetailById(surveyId);
        assertThat(closedDetailResponse.getStatus()).isEqualTo(SurveyStatus.CLOSED);
    }

    @Test
    @DisplayName("설문 삭제 후 조회 테스트")
    void deleteSurveyAndQueryTest() {
        // given
        Long surveyId = surveyService.create(authHeader, projectId, creatorId, createRequest);

        // when
        surveyService.delete(authHeader, surveyId, creatorId);

        // then
        Optional<Survey> deletedSurvey = surveyRepository.findById(surveyId);
        assertThat(deletedSurvey).isPresent();
        assertThat(deletedSurvey.get().getIsDeleted()).isTrue();

        assertThatThrownBy(() -> surveyReadService.findSurveyDetailById(surveyId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("유효하지 않은 권한으로 설문 생성 실패 테스트")
    void createSurveyWithInvalidPermissionTest() {
        // given
        ProjectValidDto invalidProject = ProjectValidDto.of(List.of(2, 3), projectId);
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(invalidProject);

        // when & then
        assertThatThrownBy(() -> surveyService.create(authHeader, projectId, creatorId, createRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("진행 중인 설문 수정 실패 테스트")
    void updateInProgressSurveyFailTest() {
        // given
        Long surveyId = surveyService.create(authHeader, projectId, creatorId, createRequest);
        surveyService.open(authHeader, surveyId, creatorId);

        // when & then
        assertThatThrownBy(() -> surveyService.update(authHeader, surveyId, creatorId, updateRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.CONFLICT);
    }

    @Test
    @DisplayName("존재하지 않는 설문 조회 실패 테스트")
    void findNonExistentSurveyFailTest() {
        // given
        Long nonExistentSurveyId = 999L;

        // when & then
        assertThatThrownBy(() -> surveyReadService.findSurveyDetailById(nonExistentSurveyId))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("설문 상태별 조회 테스트")
    void findSurveysByStatusTest() {
        // given
        Long surveyId1 = surveyService.create(authHeader, projectId, creatorId, createRequest);
        
        ReflectionTestUtils.setField(createRequest, "title", "두 번째 설문");
        Long surveyId2 = surveyService.create(authHeader, projectId, creatorId, createRequest);

        surveyService.open(authHeader, surveyId1, creatorId);

        // when
        var preparingSurveys = surveyReadService.findBySurveyStatus("PREPARING");
        
        // then
        assertThat(preparingSurveys.getSurveyIds()).contains(surveyId2);
        
        // when
        var inProgressSurveys = surveyReadService.findBySurveyStatus("IN_PROGRESS");
        
        // then
        assertThat(inProgressSurveys.getSurveyIds()).contains(surveyId1);
    }
}