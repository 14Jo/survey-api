package com.example.surveyapi.project.domain.manager;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.example.surveyapi.project.domain.participant.manager.entity.ProjectManager;
import com.example.surveyapi.project.domain.participant.manager.enums.ManagerRole;
import com.example.surveyapi.project.domain.project.entity.Project;
import com.example.surveyapi.global.exception.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

public class ProjectManagerTest {
	@Test
	void 매니저_추가_정상() {
		// given
		Project project = createProject();

		// when
		project.addManager(2L);

		// then
		assertEquals(2, project.getProjectManagers().size());
	}

	@Test
	void 매니저_중복_추가_실패() {
		// given
		Project project = createProject();
		project.addManager(2L);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.addManager(2L);
		});
		assertEquals(CustomErrorCode.ALREADY_REGISTERED_MANAGER, exception.getErrorCode());
	}

	@Test
	void 매니저_권한_변경_정상() {
		// given
		Project project = createProject();
		project.addManager(2L);
		ProjectManager manager = project.findManagerByUserId(2L);
		ReflectionTestUtils.setField(manager, "id", 2L);

		// when
		project.updateManagerRole(1L, manager.getId(), ManagerRole.WRITE);

		// then
		ProjectManager projectManager = project.findManagerById(manager.getId());
		assertEquals(ManagerRole.WRITE, projectManager.getRole());
	}

	@Test
	void 매니저_권한_변경_OWNER_로_시도_시_예외() {
		// given
		Project project = createProject();
		project.addManager(2L);
		ProjectManager manager = project.findManagerByUserId(2L);
		ReflectionTestUtils.setField(manager, "id", 2L);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.updateManagerRole(1L, manager.getId(), ManagerRole.OWNER);
		});
		assertEquals(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE, exception.getErrorCode());
	}

	@Test
	void 매니저_권한_변경_소유자가_아닌_사용자_시도_실패() {
		// given
		Project project = createProject();
		project.addManager(2L);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.updateManagerRole(2L, 1L, ManagerRole.WRITE);
		});
		assertEquals(CustomErrorCode.ACCESS_DENIED, exception.getErrorCode());
	}

	@Test
	void 매니저_권한_변경_본인_OWNER_권한_변경_시도_실패() {
		// given
		Project project = createProject();
		Long ownerManagerId = project.findManagerByUserId(1L).getId();

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.updateManagerRole(1L, ownerManagerId, ManagerRole.WRITE);
		});
		assertEquals(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE, exception.getErrorCode());
	}

	@Test
	void 매니저_권한_변경_OWNER로_변경_시도_실패() {
		// given
		Project project = createProject();
		project.addManager(2L);
		Long managerId = project.findManagerByUserId(2L).getId();

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.updateManagerRole(1L, managerId, ManagerRole.OWNER);
		});
		assertEquals(CustomErrorCode.CANNOT_CHANGE_OWNER_ROLE, exception.getErrorCode());
	}

	@Test
	void 매니저_삭제_정상() {
		// given
		Project project = createProject();
		project.addManager(2L);
		ProjectManager targetProjectManager = project.findManagerByUserId(2L);
		ReflectionTestUtils.setField(targetProjectManager, "id", 2L);

		// when
		project.deleteManager(1L, 2L);

		// then
		assertTrue(targetProjectManager.getIsDeleted());
	}

	@Test
	void 존재하지_않는_매니저_ID로_삭제_실패() {
		// given
		Project project = createProject();

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.deleteManager(1L, 999L);
		});
		assertEquals(CustomErrorCode.NOT_FOUND_MANAGER, exception.getErrorCode());
	}

	@Test
	void 매니저_삭제_본인_소유자_삭제_시도_실패() {
		// given
		Project project = createProject();
		ProjectManager ownerProjectManager = project.findManagerByUserId(1L);

		// when & then
		CustomException exception = assertThrows(CustomException.class, () -> {
			project.deleteManager(1L, ownerProjectManager.getId());
		});
		assertEquals(CustomErrorCode.CANNOT_DELETE_SELF_OWNER, exception.getErrorCode());
	}

	private Project createProject() {
		return Project.create("테스트", "설명", 1L, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
	}
}