package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.SurveyRequest;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.domain.survey.domain.survey.vo.QuestionInfo;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyDuration;
import com.example.surveyapi.domain.survey.domain.survey.vo.SurveyOption;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SurveyServiceTest {

    @Mock
    private SurveyRepository surveyRepository;

    @Mock
    private ProjectPort projectPort;

    @InjectMocks
    private SurveyService surveyService;

    private CreateSurveyRequest createRequest;
    private UpdateSurveyRequest updateRequest;
    private Survey mockSurvey;
    private ProjectValidDto validProject;
    private ProjectStateDto openProjectState;
    private String authHeader = "Bearer token";

    @BeforeEach
    void setUp() {
        // given
        createRequest = new CreateSurveyRequest();
        ReflectionTestUtils.setField(createRequest, "title", "설문 제목");
        ReflectionTestUtils.setField(createRequest, "description", "설문 설명");
        ReflectionTestUtils.setField(createRequest, "surveyType", SurveyType.VOTE);

        SurveyRequest.Duration duration = new SurveyRequest.Duration();
        ReflectionTestUtils.setField(duration, "startDate", LocalDateTime.now().plusDays(1));
        ReflectionTestUtils.setField(duration, "endDate", LocalDateTime.now().plusDays(10));
        ReflectionTestUtils.setField(createRequest, "surveyDuration", duration);

        SurveyRequest.Option option = new SurveyRequest.Option();
        ReflectionTestUtils.setField(option, "anonymous", true);
        ReflectionTestUtils.setField(option, "allowResponseUpdate", true);
        ReflectionTestUtils.setField(createRequest, "surveyOption", option);

        SurveyRequest.QuestionRequest questionRequest = new SurveyRequest.QuestionRequest();
        ReflectionTestUtils.setField(questionRequest, "content", "질문 내용");
        ReflectionTestUtils.setField(questionRequest, "questionType", QuestionType.SINGLE_CHOICE);
        ReflectionTestUtils.setField(questionRequest, "displayOrder", 1);
        ReflectionTestUtils.setField(questionRequest, "isRequired", true);
        ReflectionTestUtils.setField(createRequest, "questions", List.of(questionRequest));

        updateRequest = new UpdateSurveyRequest();
        ReflectionTestUtils.setField(updateRequest, "title", "수정된 제목");
        ReflectionTestUtils.setField(updateRequest, "description", "수정된 설명");

        mockSurvey = Survey.create(
            1L, 1L, "기존 제목", "기존 설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        ReflectionTestUtils.setField(mockSurvey, "surveyId", 1L);

        validProject = ProjectValidDto.of(List.of(1, 2, 3), 1L);
        openProjectState = ProjectStateDto.of("IN_PROGRESS");
    }

    @Test
    @DisplayName("설문 생성 - 성공")
    void createSurvey_success() {
        // given
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
        when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(openProjectState);
        when(surveyRepository.save(any(Survey.class))).thenAnswer(invocation -> {
            Survey survey = invocation.getArgument(0);
            ReflectionTestUtils.setField(survey, "surveyId", 1L);
            return survey;
        });

        // when
        Long surveyId = surveyService.create(authHeader, 1L, 1L, createRequest);

        // then
        assertThat(surveyId).isEqualTo(1L);
        verify(surveyRepository).save(any(Survey.class));
    }

    @Test
    @DisplayName("설문 생성 - 프로젝트에 참여하지 않은 사용자")
    void createSurvey_fail_invalidPermission() {
        // given
        ProjectValidDto invalidProject = ProjectValidDto.of(List.of(2, 3), 1L);
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(invalidProject);

        // when & then
        assertThatThrownBy(() -> surveyService.create(authHeader, 1L, 1L, createRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("설문 생성 - 종료된 프로젝트")
    void createSurvey_fail_closedProject() {
        // given
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
        ProjectStateDto closedProjectState = ProjectStateDto.of("CLOSED");
        when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(closedProjectState);

        // when & then
        assertThatThrownBy(() -> surveyService.create(authHeader, 1L, 1L, createRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PROJECT_STATE);
    }

    @Test
    @DisplayName("설문 수정 - 성공")
    void updateSurvey_success() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
        when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(openProjectState);

        // when
        Long surveyId = surveyService.update(authHeader, 1L, 1L, updateRequest);

        // then
        assertThat(surveyId).isEqualTo(1L);
        verify(surveyRepository).update(any(Survey.class));
    }

    @Test
    @DisplayName("설문 수정 - 진행 중인 설문")
    void updateSurvey_fail_inProgress() {
        // given
        Survey inProgressSurvey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        ReflectionTestUtils.setField(inProgressSurvey, "surveyId", 1L);
        inProgressSurvey.open();

        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(inProgressSurvey));

        // when & then
        assertThatThrownBy(() -> surveyService.update(authHeader, 1L, 1L, updateRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.CONFLICT);
    }

    @Test
    @DisplayName("설문 수정 - 존재하지 않는 설문")
    void updateSurvey_fail_notFound() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyService.update(authHeader, 1L, 1L, updateRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("설문 수정 - 권한 없음")
    void updateSurvey_fail_invalidPermission() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        ProjectValidDto invalidProject = ProjectValidDto.of(List.of(2, 3), 1L);
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(invalidProject);

        // when & then
        assertThatThrownBy(() -> surveyService.update(authHeader, 1L, 1L, updateRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("설문 수정 - 종료된 프로젝트")
    void updateSurvey_fail_closedProject() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
        ProjectStateDto closedProjectState = ProjectStateDto.of("CLOSED");
        when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(closedProjectState);

        // when & then
        assertThatThrownBy(() -> surveyService.update(authHeader, 1L, 1L, updateRequest))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PROJECT_STATE);
    }

    @Test
    @DisplayName("설문 삭제 - 성공")
    void deleteSurvey_success() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
        when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(openProjectState);

        // when
        Long surveyId = surveyService.delete(authHeader, 1L, 1L);

        // then
        assertThat(surveyId).isEqualTo(1L);
        verify(surveyRepository).delete(any(Survey.class));
    }

    @Test
    @DisplayName("설문 삭제 - 진행 중인 설문")
    void deleteSurvey_fail_inProgress() {
        // given
        Survey inProgressSurvey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        ReflectionTestUtils.setField(inProgressSurvey, "surveyId", 1L);
        inProgressSurvey.open();

        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(inProgressSurvey));

        // when & then
        assertThatThrownBy(() -> surveyService.delete(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.CONFLICT);
    }

    @Test
    @DisplayName("설문 삭제 - 존재하지 않는 설문")
    void deleteSurvey_fail_notFound() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyService.delete(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("설문 삭제 - 권한 없음")
    void deleteSurvey_fail_invalidPermission() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        ProjectValidDto invalidProject = ProjectValidDto.of(List.of(2, 3), 1L);
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(invalidProject);

        // when & then
        assertThatThrownBy(() -> surveyService.delete(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("설문 삭제 - 종료된 프로젝트")
    void deleteSurvey_fail_closedProject() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
        ProjectStateDto closedProjectState = ProjectStateDto.of("CLOSED");
        when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(closedProjectState);

        // when & then
        assertThatThrownBy(() -> surveyService.delete(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PROJECT_STATE);
    }

    @Test
    @DisplayName("설문 시작 - 성공")
    void openSurvey_success() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);

        // when
        Long surveyId = surveyService.open(authHeader, 1L, 1L);

        // then
        assertThat(surveyId).isEqualTo(1L);
        assertThat(mockSurvey.getStatus()).isEqualTo(SurveyStatus.IN_PROGRESS);
        verify(surveyRepository).stateUpdate(any(Survey.class));
    }

    @Test
    @DisplayName("설문 시작 - 준비 중이 아닌 설문")
    void openSurvey_fail_notPreparing() {
        // given
        Survey inProgressSurvey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        ReflectionTestUtils.setField(inProgressSurvey, "surveyId", 1L);
        inProgressSurvey.open();

        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(inProgressSurvey));

        // when & then
        assertThatThrownBy(() -> surveyService.open(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_STATE_TRANSITION);
    }

    @Test
    @DisplayName("설문 시작 - 존재하지 않는 설문")
    void openSurvey_fail_notFound() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyService.open(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("설문 시작 - 권한 없음")
    void openSurvey_fail_invalidPermission() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));
        ProjectValidDto invalidProject = ProjectValidDto.of(List.of(2, 3), 1L);
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(invalidProject);

        // when & then
        assertThatThrownBy(() -> surveyService.open(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PERMISSION);
    }

    @Test
    @DisplayName("설문 종료 - 성공")
    void closeSurvey_success() {
        // given
        Survey inProgressSurvey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        ReflectionTestUtils.setField(inProgressSurvey, "surveyId", 1L);
        inProgressSurvey.open();

        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(inProgressSurvey));
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);

        // when
        Long surveyId = surveyService.close(authHeader, 1L, 1L);

        // then
        assertThat(surveyId).isEqualTo(1L);
        assertThat(inProgressSurvey.getStatus()).isEqualTo(SurveyStatus.CLOSED);
        verify(surveyRepository).stateUpdate(any(Survey.class));
    }

    @Test
    @DisplayName("설문 종료 - 진행 중이 아닌 설문")
    void closeSurvey_fail_notInProgress() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(mockSurvey));

        // when & then
        assertThatThrownBy(() -> surveyService.close(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_STATE_TRANSITION);
    }

    @Test
    @DisplayName("설문 종료 - 존재하지 않는 설문")
    void closeSurvey_fail_notFound() {
        // given
        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> surveyService.close(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
    }

    @Test
    @DisplayName("설문 종료 - 권한 없음")
    void closeSurvey_fail_invalidPermission() {
        // given
        Survey inProgressSurvey = Survey.create(
            1L, 1L, "제목", "설명", SurveyType.VOTE,
            SurveyDuration.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)),
            SurveyOption.of(true, true),
            List.of()
        );
        ReflectionTestUtils.setField(inProgressSurvey, "surveyId", 1L);
        inProgressSurvey.open();

        when(surveyRepository.findBySurveyIdAndCreatorIdAndIsDeletedFalse(anyLong(), anyLong()))
            .thenReturn(Optional.of(inProgressSurvey));
        ProjectValidDto invalidProject = ProjectValidDto.of(List.of(2, 3), 1L);
        when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(invalidProject);

        // when & then
        assertThatThrownBy(() -> surveyService.close(authHeader, 1L, 1L))
            .isInstanceOf(CustomException.class)
            .hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PERMISSION);
    }
} 