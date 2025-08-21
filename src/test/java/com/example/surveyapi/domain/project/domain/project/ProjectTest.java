package com.example.surveyapi.domain.project.domain.project;

import static java.time.temporal.ChronoUnit.*;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.surveyapi.domain.project.domain.participant.manager.entity.ProjectManager;
import com.example.surveyapi.domain.project.domain.participant.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

class ProjectTest {

	@Test
	void 프로젝트_생성_정상_및_소유자_매니저_자동_등록() {
		// given
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusDays(10);

		// when
		Project project = Project.create("테스트", "설명", 1L, 50, start, end);

		// then
		assertEquals("테스트", project.getName());
		assertEquals("설명", project.getDescription());
		assertEquals(1L, project.getOwnerId());
		assertEquals(ProjectState.PENDING, project.getState());
		assertEquals(1, project.getProjectManagers().size());
		assertEquals(ManagerRole.OWNER, project.getProjectManagers().get(0).getRole());
		assertEquals(1L, project.getProjectManagers().get(0).getUserId());
	}

	@Test
	void 프로젝트_정보_수정_정상() {
		// given
		Project project = createProject();

		// when
		project.updateProject("수정된이름", "수정된설명", LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(7));

		// then
		assertEquals("수정된이름", project.getName());
		assertEquals("수정된설명", project.getDescription());
		assertEquals(LocalDateTime.now().plusDays(2).getDayOfMonth(),
			project.getPeriod().getPeriodStart().getDayOfMonth());
		assertEquals(LocalDateTime.now().plusDays(7).getDayOfMonth(),
			project.getPeriod().getPeriodEnd().getDayOfMonth());
	}

	@Test
	void 프로젝트_정보_수정_빈_문자열_무시() {
		// given
		Project project = createProject();
		String originalName = project.getName();
		String originalDescription = project.getDescription();

		// when
		project.updateProject("", "", null, null);

		// then
		assertEquals(originalName, project.getName());
		assertEquals(originalDescription, project.getDescription());
	}

	@Test
	void 프로젝트_상태_IN_PROGRESS_로_변경() {
		// given
		Project project = createProject();

		// when
		project.updateState(ProjectState.IN_PROGRESS);

		// then
		assertEquals(ProjectState.IN_PROGRESS, project.getState());
		assertThat(project.getPeriod().getPeriodStart())
			.isCloseTo(LocalDateTime.now(), within(2, SECONDS));
	}

	@Test
	void 프로젝트_상태_CLOSED_로_변경() {
		// given
		Project project = createProject();
		project.updateState(ProjectState.IN_PROGRESS);

		// when
		project.updateState(ProjectState.CLOSED);

		// then
		assertEquals(ProjectState.CLOSED, project.getState());
		assertThat(project.getPeriod().getPeriodEnd())
			.isCloseTo(LocalDateTime.now(), within(2, SECONDS));
	}

	@Test
	void 프로젝트_상태_변경_PENDING_에서_CLOSED_로_직접_변경_불가() {
		// given
		Project project = createProject();

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.updateState(ProjectState.CLOSED);
		});
		assertEquals(CustomErrorCode.INVALID_STATE_TRANSITION, exception.getErrorCode());
	}

	@Test
	void 프로젝트_상태_변경_IN_PROGRESS_에서_PENDING_으로_변경_불가() {
		// given
		Project project = createProject();
		project.updateState(ProjectState.IN_PROGRESS);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.updateState(ProjectState.PENDING);
		});
		assertEquals(CustomErrorCode.INVALID_STATE_TRANSITION, exception.getErrorCode());
	}

	@Test
	void 프로젝트_소유자_위임_정상() {
		// given
		Project project = createProject();
		project.addManager(2L);

		// when
		project.updateOwner(1L, 2L);

		// then
		ProjectManager newOwner = project.findManagerByUserId(2L);
		ProjectManager previousOwner = project.findManagerByUserId(1L);

		assertEquals(ManagerRole.OWNER, newOwner.getRole());
		assertEquals(ManagerRole.READ, previousOwner.getRole());
	}

	@Test
	void 프로젝트_소프트_삭제_정상() {
		// given
		Project project = createProject();
		project.addManager(2L);

		// when
		project.softDelete(1L);

		// then
		assertEquals(ProjectState.CLOSED, project.getState());
		assertTrue(project.getIsDeleted());
		assertTrue(project.getProjectManagers().stream().allMatch(ProjectManager::getIsDeleted));
	}

	private Project createProject() {
		return Project.create("테스트", "설명", 1L, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
	}
}
