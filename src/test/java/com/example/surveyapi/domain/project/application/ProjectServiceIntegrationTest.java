package com.example.surveyapi.domain.project.application;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.project.application.dto.request.CreateGroupRequest;
import com.example.surveyapi.domain.project.application.dto.request.CreateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectRequest;
import com.example.surveyapi.domain.project.application.dto.request.UpdateProjectStateRequest;
import com.example.surveyapi.domain.project.application.dto.response.CreateGroupResponse;
import com.example.surveyapi.domain.project.application.dto.response.ProjectInfoResponse;
import com.example.surveyapi.domain.project.domain.group.entity.Group;
import com.example.surveyapi.domain.project.domain.group.enums.AgeGroup;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.domain.project.infra.project.jpa.ProjectJpaRepository;

@SpringBootTest
@TestPropertySource(properties = "SECRET_KEY=12345678901234567890123456789012")
@Transactional
class ProjectServiceIntegrationTest {

	@Autowired
	private ProjectService projectService;

	@Autowired
	private ProjectJpaRepository projectRepository;

	@Test
	void 프로젝트_생성시_DB에_저장된다() {
		// given
		Long projectId = createSampleProject();

		// when
		Project project = projectRepository.findById(projectId).orElseThrow();

		// then
		assertThat(project.getName()).isEqualTo("테스트 프로젝트");
		assertThat(project.getDescription()).isEqualTo("설명");
		assertThat(project.getOwnerId()).isEqualTo(1L);
	}

	@Test
	void 프로젝트_조회시_삭제된_프로젝트는_포함되지_않는다() {
		// given
		Long projectId = createSampleProject();

		// when
		projectService.deleteProject(projectId, 1L);

		// then
		List<ProjectInfoResponse> result = projectService.getMyProjects(1L);
		assertThat(result).isEmpty();
	}

	@Test
	void 프로젝트_수정시_DB에_변경내용이_반영된다() {
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
		Project updated = projectRepository.findById(projectId).orElseThrow();
		assertThat(updated.getName()).isEqualTo("수정된 이름");
		assertThat(updated.getDescription()).isEqualTo("수정된 설명");
	}

	@Test
	void 프로젝트_삭제시_DB에서_소프트삭제된다() {
		// given
		Long projectId = createSampleProject();

		// when
		projectService.deleteProject(projectId, 1L);

		// then
		Project deleted = projectRepository.findById(projectId).orElseThrow();
		assertThat(deleted.getIsDeleted()).isTrue();
		assertThat(deleted.getState()).isEqualTo(ProjectState.CLOSED);
	}

	@Test
	void 상태변경후_DB값_확인() {
		// given
		Long projectId = createSampleProject();
		UpdateProjectStateRequest request = new UpdateProjectStateRequest();
		ReflectionTestUtils.setField(request, "state", ProjectState.IN_PROGRESS);

		// when
		projectService.updateState(projectId, request);

		// then
		Project updated = projectRepository.findById(projectId).orElseThrow();
		assertThat(updated.getState()).isEqualTo(ProjectState.IN_PROGRESS);
	}

	@Test
	void 삭제후_DB값_확인() {
		// given
		Long projectId = createSampleProject();

		// when
		projectService.deleteProject(projectId, 1L);

		// then
		Project deleted = projectRepository.findById(projectId).orElseThrow();
		assertThat(deleted.getIsDeleted()).isTrue();
		assertThat(deleted.getState()).isEqualTo(ProjectState.CLOSED);
	}

	@Test
	void 그룹_생성시_DB에_저장된다() {
		// given
		Long projectId = createSampleProject();

		CreateGroupRequest request = new CreateGroupRequest();
		ReflectionTestUtils.setField(request, "ageGroup", AgeGroup.OTHERS);

		// when
		CreateGroupResponse response = projectService.createGroup(projectId, request);

		// then
		Project project = projectRepository.findById(projectId).orElseThrow();
		assertThat(project.getGroups()).hasSize(1);

		Group group = project.getGroups().get(0);
		assertThat(group.getAgeGroup()).isEqualTo(AgeGroup.OTHERS);
		assertThat(response.getGroupId()).isEqualTo(group.getId());
		assertThat(response.getGroupName()).isEqualTo(AgeGroup.OTHERS.getGroupName());
	}

	private Long createSampleProject() {
		CreateProjectRequest request = new CreateProjectRequest();
		ReflectionTestUtils.setField(request, "name", "테스트 프로젝트");
		ReflectionTestUtils.setField(request, "description", "설명");
		ReflectionTestUtils.setField(request, "maxMembers", 50);
		ReflectionTestUtils.setField(request, "periodStart", LocalDateTime.now());
		ReflectionTestUtils.setField(request, "periodEnd", LocalDateTime.now().plusDays(5));

		return projectService.createProject(request, 1L).getProjectId();
	}
}
