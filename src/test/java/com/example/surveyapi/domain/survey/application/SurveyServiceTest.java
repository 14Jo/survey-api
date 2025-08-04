package com.example.surveyapi.domain.survey.application;

import com.example.surveyapi.domain.survey.application.client.ProjectPort;
import com.example.surveyapi.domain.survey.application.client.ProjectStateDto;
import com.example.surveyapi.domain.survey.application.client.ProjectValidDto;
import com.example.surveyapi.domain.survey.application.request.CreateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.UpdateSurveyRequest;
import com.example.surveyapi.domain.survey.application.request.SurveyRequest;
import com.example.surveyapi.domain.survey.domain.question.enums.QuestionType;
import com.example.surveyapi.domain.survey.domain.survey.Survey;
import com.example.surveyapi.domain.survey.domain.survey.SurveyRepository;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyType;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SurveyServiceTest {

	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}

	@Autowired
	private SurveyService surveyService;

	@Autowired
	private SurveyRepository surveyRepository;

	@MockitoBean
	private ProjectPort projectPort;

	private CreateSurveyRequest createRequest;
	private UpdateSurveyRequest updateRequest;
	private final String authHeader = "Bearer token";
	private final Long creatorId = 1L;
	private final Long projectId = 1L;

	@BeforeEach
	void setUp() {
		ProjectValidDto validProject = ProjectValidDto.of(List.of(creatorId.intValue()), projectId);
		ProjectStateDto openProjectState = ProjectStateDto.of("IN_PROGRESS");
		when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(validProject);
		when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(openProjectState);

		createRequest = new CreateSurveyRequest();
		ReflectionTestUtils.setField(createRequest, "title", "새로운 설문 제목");
		ReflectionTestUtils.setField(createRequest, "description", "설문 설명입니다.");
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
		ReflectionTestUtils.setField(createRequest, "questions", List.of(questionRequest));

		updateRequest = new UpdateSurveyRequest();
		ReflectionTestUtils.setField(updateRequest, "title", "수정된 설문 제목");
		ReflectionTestUtils.setField(updateRequest, "description", "수정된 설문 설명입니다.");
	}

	@Test
	@DisplayName("설문 생성 - 성공")
	void createSurvey_success() {
		// when
		Long surveyId = surveyService.create(authHeader, creatorId, projectId, createRequest);

		// then
		Optional<Survey> foundSurvey = surveyRepository.findById(surveyId);
		assertThat(foundSurvey).isPresent();
		assertThat(foundSurvey.get().getTitle()).isEqualTo("새로운 설문 제목");
		assertThat(foundSurvey.get().getCreatorId()).isEqualTo(creatorId);
	}

	@Test
	@DisplayName("설문 생성 - 실패 (프로젝트에 참여하지 않은 사용자)")
	void createSurvey_fail_invalidPermission() {
		// given
		ProjectValidDto invalidProject = ProjectValidDto.of(List.of(2, 3), projectId);
		when(projectPort.getProjectMembers(anyString(), anyLong(), anyLong())).thenReturn(invalidProject);

		// when & then
		assertThatThrownBy(() -> surveyService.create(authHeader, creatorId, projectId, createRequest))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PERMISSION);
	}

	@Test
	@DisplayName("설문 생성 - 실패 (종료된 프로젝트)")
	void createSurvey_fail_closedProject() {
		// given
		ProjectStateDto closedProjectState = ProjectStateDto.of("CLOSED");
		when(projectPort.getProjectState(anyString(), anyLong())).thenReturn(closedProjectState);

		// when & then
		assertThatThrownBy(() -> surveyService.create(authHeader, creatorId, projectId, createRequest))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_PROJECT_STATE);
	}

	@Test
	@DisplayName("설문 수정 - 성공")
	void updateSurvey_success() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "기존 제목", "기존 설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));

		// when
		surveyService.update(authHeader, savedSurvey.getSurveyId(), creatorId, updateRequest);

		// then
		Survey updatedSurvey = surveyRepository.findById(savedSurvey.getSurveyId()).orElseThrow();
		assertThat(updatedSurvey.getTitle()).isEqualTo("수정된 설문 제목");
		assertThat(updatedSurvey.getDescription()).isEqualTo("수정된 설문 설명입니다.");
	}

	@Test
	@DisplayName("설문 수정 - 실패 (진행 중인 설문)")
	void updateSurvey_fail_inProgress() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "제목", "설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));
		savedSurvey.open();
		surveyRepository.save(savedSurvey);

		// when & then
		assertThatThrownBy(() -> surveyService.update(authHeader, savedSurvey.getSurveyId(), creatorId, updateRequest))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.CONFLICT);
	}

	@Test
	@DisplayName("설문 수정 - 실패 (존재하지 않는 설문)")
	void updateSurvey_fail_notFound() {
		// given
		Long nonExistentSurveyId = 999L;

		// when & then
		assertThatThrownBy(() -> surveyService.update(authHeader, nonExistentSurveyId, creatorId, updateRequest))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.NOT_FOUND_SURVEY);
	}

	@Test
	@DisplayName("설문 삭제 - 성공")
	void deleteSurvey_success() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "삭제될 설문", "설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));

		// when
		surveyService.delete(authHeader, savedSurvey.getSurveyId(), creatorId);

		// then
		Survey deletedSurvey = surveyRepository.findById(savedSurvey.getSurveyId()).orElseThrow();
		assertThat(deletedSurvey.getIsDeleted()).isTrue();
	}

	@Test
	@DisplayName("설문 삭제 - 실패 (진행 중인 설문)")
	void deleteSurvey_fail_inProgress() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "제목", "설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));
		savedSurvey.open();
		surveyRepository.save(savedSurvey);

		// when & then
		assertThatThrownBy(() -> surveyService.delete(authHeader, savedSurvey.getSurveyId(), creatorId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.CONFLICT);
	}

	@Test
	@DisplayName("설문 시작 - 성공")
	void openSurvey_success() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "시작될 설문", "설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));

		// when
		surveyService.open(authHeader, savedSurvey.getSurveyId(), creatorId);

		// then
		Survey openedSurvey = surveyRepository.findById(savedSurvey.getSurveyId()).orElseThrow();
		assertThat(openedSurvey.getStatus()).isEqualTo(SurveyStatus.IN_PROGRESS);
	}

	@Test
	@DisplayName("설문 시작 - 실패 (준비 중이 아닌 설문)")
	void openSurvey_fail_notPreparing() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "제목", "설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));
		savedSurvey.open();
		surveyRepository.save(savedSurvey);

		// when & then
		assertThatThrownBy(() -> surveyService.open(authHeader, savedSurvey.getSurveyId(), creatorId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_STATE_TRANSITION);
	}

	@Test
	@DisplayName("설문 종료 - 성공")
	void closeSurvey_success() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "종료될 설문", "설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));
		savedSurvey.open();
		surveyRepository.save(savedSurvey);

		// when
		surveyService.close(authHeader, savedSurvey.getSurveyId(), creatorId);

		// then
		Survey closedSurvey = surveyRepository.findById(savedSurvey.getSurveyId()).orElseThrow();
		assertThat(closedSurvey.getStatus()).isEqualTo(SurveyStatus.CLOSED);
	}

	@Test
	@DisplayName("설문 종료 - 실패 (진행 중이 아닌 설문)")
	void closeSurvey_fail_notInProgress() {
		// given
		Survey savedSurvey = surveyRepository.save(Survey.create(projectId, creatorId, "제목", "설명", SurveyType.VOTE,
			createRequest.getSurveyDuration().toSurveyDuration(), createRequest.getSurveyOption().toSurveyOption(), List.of()));

		// when & then
		assertThatThrownBy(() -> surveyService.close(authHeader, savedSurvey.getSurveyId(), creatorId))
			.isInstanceOf(CustomException.class)
			.hasFieldOrPropertyWithValue("errorCode", CustomErrorCode.INVALID_STATE_TRANSITION);
	}
}