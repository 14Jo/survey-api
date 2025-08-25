package com.example.surveyapi.project.api;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import com.example.surveyapi.project.application.ProjectQueryService;
import com.example.surveyapi.project.application.ProjectService;
import com.example.surveyapi.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.project.application.dto.response.CreateProjectResponse;
import com.example.surveyapi.project.application.dto.response.ProjectInfoResponse;
import com.example.surveyapi.project.application.dto.response.ProjectMemberIdsResponse;
import com.example.surveyapi.project.domain.participant.manager.enums.ManagerRole;
import com.example.surveyapi.project.domain.project.entity.Project;
import com.example.surveyapi.project.domain.project.enums.ProjectState;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebMvcTest(ProjectController.class)
@ActiveProfiles("test")
@Import(ProjectControllerTest.TestSecurityConfig.class)
class ProjectControllerTest {

	@Autowired
	private MockMvc mockMvc;
	@MockitoBean
	private ProjectService projectService;
	@MockitoBean
	private ProjectQueryService projectQueryService;
	@Autowired
	private ObjectMapper objectMapper;
	private CreateProjectRequest createRequest;
	private UpdateProjectRequest updateRequest;
	private UpdateProjectStateRequest stateRequest;
	private UpdateProjectOwnerRequest ownerRequest;
	private UpdateManagerRoleRequest roleRequest;

	private Authentication auth() {
		return new UsernamePasswordAuthenticationToken(
			1L, null, List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);
	}

	@BeforeEach
	void setUp() {
		objectMapper.registerModule(new JavaTimeModule());

		createRequest = new CreateProjectRequest();
		ReflectionTestUtils.setField(createRequest, "name", "테스트 프로젝트");
		ReflectionTestUtils.setField(createRequest, "description", "설명");
		ReflectionTestUtils.setField(createRequest, "periodStart", LocalDateTime.now().plusDays(1));
		ReflectionTestUtils.setField(createRequest, "periodEnd", LocalDateTime.now().plusDays(10));
		ReflectionTestUtils.setField(createRequest, "maxMembers", 50);

		updateRequest = new UpdateProjectRequest();
		ReflectionTestUtils.setField(updateRequest, "name", "수정된 이름");
		ReflectionTestUtils.setField(updateRequest, "description", "수정된 설명");

		stateRequest = new UpdateProjectStateRequest();
		ReflectionTestUtils.setField(stateRequest, "state", ProjectState.IN_PROGRESS);

		ownerRequest = new UpdateProjectOwnerRequest();
		ReflectionTestUtils.setField(ownerRequest, "newOwnerId", 2L);

		roleRequest = new UpdateManagerRoleRequest();
		ReflectionTestUtils.setField(roleRequest, "newRole", ManagerRole.WRITE);
	}

	@Test
	@DisplayName("프로젝트 생성 - 201 반환")
	void createProject_created() throws Exception {
		// given
		when(projectService.createProject(any(CreateProjectRequest.class), anyLong()))
			.thenReturn(CreateProjectResponse.of(1L, 50));

		// when & then
		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest))
				.with(authentication(auth())))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.projectId").value(1))
			.andExpect(jsonPath("$.data.maxMembers").value(50));
	}

	@Test
	@DisplayName("프로젝트 생성 - 유효성 실패시 400")
	void createProject_validationFail_badRequest() throws Exception {
		// given
		ReflectionTestUtils.setField(createRequest, "name", "");

		// when & then
		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest))
				.with(authentication(auth())))
			.andExpect(status().isBadRequest());
	}

	@Test
	@DisplayName("프로젝트 상세 조회 - 200 반환")
	void getProject_ok() throws Exception {
		// given
		Project project = Project.create(
			"테스트 프로젝트", "설명", 1L, 50,
			LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)
		);
		ReflectionTestUtils.setField(project, "id", 1L);

		when(projectQueryService.getProject(eq(1L))).thenReturn(ProjectInfoResponse.from(project));

		// when & then
		mockMvc.perform(get("/api/projects/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.projectId").value(1))
			.andExpect(jsonPath("$.data.name").value("테스트 프로젝트"));
	}

	@Test
	@DisplayName("프로젝트 상태 변경 - 200 반환")
	void updateState_ok() throws Exception {
		// given
		// stateRequest setUp에서 생성됨

		// when & then
		mockMvc.perform(patch("/api/projects/1/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(stateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("프로젝트 정보 수정 - 200 반환")
	void updateProject_ok() throws Exception {
		// given
		// updateRequest setUp에서 생성됨

		// when & then
		mockMvc.perform(put("/api/projects/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("프로젝트 매니저 참여 - 200 반환")
	void joinProjectManager_ok() throws Exception {
		// when & then
		mockMvc.perform(post("/api/projects/1/managers")
				.with(authentication(auth())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("매니저 권한 변경 - 200 반환")
	void updateManagerRole_ok() throws Exception {
		// given
		// roleRequest setUp에서 생성됨

		// when & then
		mockMvc.perform(patch("/api/projects/1/managers/10/role")
				.with(authentication(auth()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleRequest)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("프로젝트 멤버 참여/조회/탈퇴 - 200 반환")
	void projectMember_flow_ok() throws Exception {
		// given
		Project project = Project.create(
			"테스트 프로젝트", "설명", 1L, 50,
			LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(10)
		);
		project.addMember(1L);

		when(projectQueryService.getProjectMemberIds(eq(1L)))
			.thenReturn(ProjectMemberIdsResponse.from(project));

		// when & then
		mockMvc.perform(post("/api/projects/1/members").with(authentication(auth())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));

		// when & then
		mockMvc.perform(get("/api/projects/1/members"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.currentMemberCount").value(1))
			.andExpect(jsonPath("$.data.maxMembers").value(50));

		// when & then
		mockMvc.perform(delete("/api/projects/1/members").with(authentication(auth())))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("프로젝트 검색 - 200 반환")
	void searchProjects_ok() throws Exception {
		// given
		when(projectQueryService.searchProjects(any(), any()))
			.thenReturn(new SliceImpl<>(List.of(), PageRequest.of(0, 10), false));

		// when & then
		mockMvc.perform(get("/api/projects/search").param("keyword", "테스트"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true));
	}

	@Test
	@DisplayName("프로젝트 생성 - 중복 이름이면 400")
	void createProject_duplicateName_badRequest() throws Exception {
		// given
		when(projectService.createProject(any(CreateProjectRequest.class), anyLong()))
			.thenThrow(new CustomException(CustomErrorCode.DUPLICATE_PROJECT_NAME));

		// when & then
		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createRequest))
				.with(authentication(auth())))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.DUPLICATE_PROJECT_NAME.getMessage()));
	}

	@Test
	@DisplayName("프로젝트 상세 조회 - 존재하지 않으면 404")
	void getProject_notFound_404() throws Exception {
		// given
		when(projectQueryService.getProject(eq(999L)))
			.thenThrow(new CustomException(CustomErrorCode.NOT_FOUND_PROJECT));

		// when & then
		mockMvc.perform(get("/api/projects/999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.NOT_FOUND_PROJECT.getMessage()));
	}

	@Test
	@DisplayName("프로젝트 수정 - 대상 없음 404")
	void updateProject_notFound_404() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.NOT_FOUND_PROJECT))
			.when(projectService).updateProject(eq(999L), any(UpdateProjectRequest.class));

		// when & then
		mockMvc.perform(put("/api/projects/999")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(updateRequest)))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.NOT_FOUND_PROJECT.getMessage()));
	}

	@Test
	@DisplayName("프로젝트 상태 변경 - 잘못된 전이 400")
	void updateState_invalidTransition_400() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.INVALID_STATE_TRANSITION))
			.when(projectService).updateState(eq(1L), any(UpdateProjectStateRequest.class));

		// when & then
		mockMvc.perform(patch("/api/projects/1/state")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(stateRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.INVALID_STATE_TRANSITION.getMessage()));
	}

	@Test
	@DisplayName("프로젝트 소유자 위임 - 자기 자신에게 위임 400")
	void updateOwner_selfTransfer_400() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.CANNOT_TRANSFER_TO_SELF))
			.when(projectService).updateOwner(eq(1L), any(UpdateProjectOwnerRequest.class), anyLong());

		// when & then
		mockMvc.perform(patch("/api/projects/1/owner")
				.with(authentication(auth()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(ownerRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.CANNOT_TRANSFER_TO_SELF.getMessage()));
	}

	@Test
	@DisplayName("프로젝트 삭제 - 권한 없음 403")
	void deleteProject_forbidden_403() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.ACCESS_DENIED))
			.when(projectService).deleteProject(eq(1L), anyLong());

		// when & then
		mockMvc.perform(delete("/api/projects/1").with(authentication(auth())))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.ACCESS_DENIED.getMessage()));
	}

	@Test
	@DisplayName("매니저 참여 - 이미 등록 409")
	void joinProjectManager_conflict_409() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.ALREADY_REGISTERED_MANAGER))
			.when(projectService).joinProjectManager(eq(1L), anyLong());

		// when & then
		mockMvc.perform(post("/api/projects/1/managers").with(authentication(auth())))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.ALREADY_REGISTERED_MANAGER.getMessage()));
	}

	@Test
	@DisplayName("매니저 권한 변경 - OWNER로 변경 불가 400")
	void updateManagerRole_cannotChangeOwner_400() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE))
			.when(projectService).updateManagerRole(eq(1L), eq(10L), any(UpdateManagerRoleRequest.class), anyLong());

		// when & then
		mockMvc.perform(patch("/api/projects/1/managers/10/role")
				.with(authentication(auth()))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(roleRequest)))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE.getMessage()));
	}

	@Test
	@DisplayName("매니저 삭제 - 본인 OWNER 삭제 불가 400")
	void deleteManager_cannotDeleteSelfOwner_400() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.CANNOT_DELETE_SELF_OWNER))
			.when(projectService).deleteManager(eq(1L), eq(10L), anyLong());

		// when & then
		mockMvc.perform(delete("/api/projects/1/managers/10").with(authentication(auth())))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.CANNOT_DELETE_SELF_OWNER.getMessage()));
	}

	@Test
	@DisplayName("멤버 참여 - 인원수 초과 409")
	void joinProjectMember_limitExceeded_409() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.PROJECT_MEMBER_LIMIT_EXCEEDED))
			.when(projectService).joinProjectMember(eq(1L), anyLong());

		// when & then
		mockMvc.perform(post("/api/projects/1/members").with(authentication(auth())))
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.PROJECT_MEMBER_LIMIT_EXCEEDED.getMessage()));
	}

	@Test
	@DisplayName("멤버 조회 - 프로젝트 없음 404")
	void getProjectMemberIds_notFound_404() throws Exception {
		// given
		when(projectQueryService.getProjectMemberIds(eq(999L)))
			.thenThrow(new CustomException(CustomErrorCode.NOT_FOUND_PROJECT));

		// when & then
		mockMvc.perform(get("/api/projects/999/members"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.NOT_FOUND_PROJECT.getMessage()));
	}

	@Test
	@DisplayName("멤버 탈퇴 - 멤버 아님 404")
	void leaveProjectMember_notMember_404() throws Exception {
		// given
		doThrow(new CustomException(CustomErrorCode.NOT_FOUND_MEMBER))
			.when(projectService).leaveProjectMember(eq(1L), anyLong());

		// when & then
		mockMvc.perform(delete("/api/projects/1/members").with(authentication(auth())))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").value(CustomErrorCode.NOT_FOUND_MEMBER.getMessage()));
	}

	@Test
	@DisplayName("검색 - 키워드 길이 짧으면 400 (검증 오류 맵 포함)")
	void searchProjects_keywordTooShort_400() throws Exception {
		// given: request param keyword=aa (size < 3)

		// when & then
		mockMvc.perform(get("/api/projects/search").param("keyword", "aa"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.message").exists())
			.andExpect(jsonPath("$.data.keyword").exists());
	}

	@Test
	@DisplayName("프로젝트 생성 - 잘못된 JSON 400")
	void createProject_invalidJson_400() throws Exception {
		// when & then
		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{ invalid json }"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("프로젝트 생성 - 지원하지 않는 Content-Type 415")
	void createProject_unsupportedMediaType_415() throws Exception {
		// when & then
		mockMvc.perform(post("/api/projects")
				.contentType(MediaType.TEXT_PLAIN)
				.content("plain text"))
			.andExpect(status().isUnsupportedMediaType())
			.andExpect(jsonPath("$.success").value(false));
	}

	@Test
	@DisplayName("PathVariable 타입 오류 - 500 처리")
	void pathVariable_typeMismatch_500() throws Exception {
		// when & then
		mockMvc.perform(get("/api/projects/invalid"))
			.andExpect(status().isInternalServerError())
			.andExpect(jsonPath("$.success").value(false));
	}

	@EnableWebSecurity
	static class TestSecurityConfig {
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
					.requestMatchers("/api/projects/**").permitAll()
					.anyRequest().permitAll()
				);
			return http.build();
		}
	}
}
