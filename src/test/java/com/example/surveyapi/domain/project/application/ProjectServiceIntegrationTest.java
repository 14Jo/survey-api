package com.example.surveyapi.domain.project.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateManagerRoleRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectOwnerRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.ProjectMemberIdsResponse;
import com.example.surveyapi.domain.project.domain.manager.entity.ProjectManager;
import com.example.surveyapi.domain.project.domain.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.infra.project.jpa.ProjectJpaRepository;

/**
 * DB에 정상적으로 반영되는지 확인하기 위한 통합 테스트
 * 예외 로직은 도메인 단위테스트 진행
 */
@SpringBootTest
@Transactional
class ProjectServiceIntegrationTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectJpaRepository projectRepository;

	@Test
	void 프로젝트_생성시_DB에_정상_저장() {
		// given & when
		Long projectId = createSampleProject();

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getName()).isEqualTo("테스트 프로젝트");
		assertThat(project.getDescription()).isEqualTo("설명");
		assertThat(project.getOwnerId()).isEqualTo(1L);
	}

	@Test
	void 프로젝트_정보수정시_DB값_정상_반영() {
		// given
		Long projectId = createSampleProject();
		UpdateProjectRequest request = new UpdateProjectRequest();
		ReflectionTestUtils.setField(request, "name", "수정된 이름");
		ReflectionTestUtils.setField(request, "description", "수정된 설명");
		ReflectionTestUtils.setField(request, "periodStart", LocalDateTime.now());
		ReflectionTestUtils.setField(request, "periodEnd", LocalDateTime.now().plusDays(5));

		// when
		projectService.updateProject(projectId, request);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getName()).isEqualTo("수정된 이름");
		assertThat(project.getDescription()).isEqualTo("수정된 설명");
	}

	@Test
	void 프로젝트_상태변경_IN_PROGRESS_정상동작() {
		// given
		Long projectId = createSampleProject();
		UpdateProjectStateRequest request = new UpdateProjectStateRequest();
		ReflectionTestUtils.setField(request, "state", ProjectState.IN_PROGRESS);

		// when
		projectService.updateState(projectId, request);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getState()).isEqualTo(ProjectState.IN_PROGRESS);
	}

	@Test
	void 프로젝트_삭제시_소프트삭제_정상동작() {
		// given
		Long projectId = createSampleProject();

		// when
		projectService.deleteProject(projectId, 1L);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getIsDeleted()).isTrue();
		assertThat(project.getState()).isEqualTo(ProjectState.CLOSED);
	}

	@Test
	void 프로젝트_매니저_추가_정상동작() {
		// given
		Long projectId = createSampleProject();

		// when
		projectService.joinProjectManager(projectId, 2L);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getProjectManagers().size()).isEqualTo(2); // owner + 1명
		assertThat(project.getProjectManagers().get(1).getUserId()).isEqualTo(2L);
	}

	@Test
	void 프로젝트_매니저_권한_변경_정상동작() {
		// given
		Long projectId = createSampleProject();
		projectService.joinProjectManager(projectId, 2L);

		UpdateManagerRoleRequest roleRequest = new UpdateManagerRoleRequest();
		ReflectionTestUtils.setField(roleRequest, "newRole", ManagerRole.WRITE);

		// when
		projectService.updateManagerRole(projectId, 2L, roleRequest, 1L);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		ProjectManager manager = project.getProjectManagers().get(1);
		assertThat(manager.getRole()).isEqualTo(ManagerRole.WRITE);
	}

	@Test
	void 프로젝트_매니저_삭제_정상동작() {
		// given
		Long projectId = createSampleProject();
		projectService.joinProjectManager(projectId, 2L);

		// when
		projectService.deleteManager(projectId, 2L, 1L);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getProjectManagers().get(1).getIsDeleted()).isTrue();
	}

	@Test
	void 프로젝트_소유자_위임_정상동작() {
		// given
		Long projectId = createSampleProject();
		projectService.joinProjectManager(projectId, 2L);

		UpdateProjectOwnerRequest ownerRequest = new UpdateProjectOwnerRequest();
		ReflectionTestUtils.setField(ownerRequest, "newOwnerId", 2L);

		// when
		projectService.updateOwner(projectId, ownerRequest, 1L);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getProjectManagers().stream()
			.filter(m -> m.getUserId().equals(1L)).findFirst().orElseThrow().getRole()).isEqualTo(ManagerRole.READ);
		assertThat(project.getProjectManagers().stream()
			.filter(m -> m.getUserId().equals(2L)).findFirst().orElseThrow().getRole()).isEqualTo(ManagerRole.OWNER);
	}

	@Test
	void DB에_프로젝트_멤버_정상_등록() {
		// given
		Long projectId = createSampleProject();

		// when
		projectService.joinProjectMember(projectId, 2L);
		projectService.joinProjectMember(projectId, 3L);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getCurrentMemberCount()).isEqualTo(2);
	}

	@Test
	void 프로젝트_참여인원_ID_리스트_정상_조회() {
		// given
		Long projectId = createSampleProject();
		projectService.joinProjectMember(projectId, 2L);
		projectService.joinProjectMember(projectId, 3L);
		projectService.joinProjectMember(projectId, 4L);

		// when
		ProjectMemberIdsResponse response = projectService.getProjectMemberIds(projectId);

		// then
		assertThat(response.getCurrentMemberCount()).isEqualTo(3);
		assertThat(response.getMaxMembers()).isEqualTo(50);
		assertThat(response.getMemberIds()).containsExactlyInAnyOrder(2L, 3L, 4L);
	}

	@Test
	void 프로젝트_멤버_탈퇴_정상동작() {
		// given
		Long projectId = createSampleProject();
		projectService.joinProjectMember(projectId, 2L);
		projectService.joinProjectMember(projectId, 3L);

		// when
		projectService.leaveProject(projectId, 2L);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getCurrentMemberCount()).isEqualTo(1);
	}

	private Long createSampleProject() {
		CreateProjectRequest request = new CreateProjectRequest();
		ReflectionTestUtils.setField(request, "name", "테스트 프로젝트");
		ReflectionTestUtils.setField(request, "description", "설명");
		ReflectionTestUtils.setField(request, "periodStart", LocalDateTime.now());
		ReflectionTestUtils.setField(request, "periodEnd", LocalDateTime.now().plusDays(5));
		ReflectionTestUtils.setField(request, "maxMembers", 50);
		return projectService.createProject(request, 1L).getProjectId();
	}
}
