package com.example.surveyapi.domain.project.domain.project;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.surveyapi.domain.project.domain.manager.enums.ManagerRole;
import com.example.surveyapi.domain.project.domain.project.enums.ProjectState;

class ProjectTest {

	@Test
	void 프로젝트_생성_정상_및_소유자_매니저_자동_등록() {
		// given
		LocalDateTime start = LocalDateTime.now();
		LocalDateTime end = start.plusDays(10);

		// when
		Project project = Project.create("테스트", "설명", 1L, start, end);

		// then
		assertEquals("테스트", project.getName());
		assertEquals("설명", project.getDescription());
		assertEquals(1L, project.getOwnerId());
		assertEquals(ProjectState.PENDING, project.getState());
		assertEquals(1, project.getManagers().size());
		assertEquals(ManagerRole.OWNER, project.getManagers().get(0).getRole());
		assertEquals(1L, project.getManagers().get(0).getUserId());
	}

	@Test
	void 프로젝트_정보_수정_정상() {
		// given
		Project project = Project.create("테스트", "설명", 1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5));

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
		Project project = Project.create("테스트", "설명", 1L, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
		String originalName = project.getName();
		String originalDescription = project.getDescription();

		// when
		project.updateProject("", "", null, null);

		// then
		assertEquals(originalName, project.getName());
		assertEquals(originalDescription, project.getDescription());
	}
}