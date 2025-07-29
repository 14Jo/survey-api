package com.example.surveyapi.domain.project.domain.group;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.surveyapi.domain.project.domain.group.enums.AgeGroup;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.global.exception.CustomException;

public class GroupTest {
	@Test
	void 그룹_생성_정상() {
		// given
		Project project = createProject();

		// when
		project.addGroup(1L, AgeGroup.TWENTIES);

		// then
		assertEquals(1, project.getGroups().size());
	}

	@Test
	void 그룹_중복_생성_시_예외() {
		// given
		Project project = createProject();
		project.addGroup(1L, AgeGroup.TWENTIES);

		// when & then
		assertThrows(CustomException.class,
			() -> project.addGroup(1L, AgeGroup.TWENTIES));
	}

	@Test
	void READ_권한_사용자가_그룹_생성_시_예외() {
		// given
		Project project = createProject();
		project.addManager(1L, 2L);

		// when & then
		assertThrows(CustomException.class,
			() -> project.addGroup(2L, AgeGroup.OTHERS));
	}

	private Project createProject() {
		return Project.create("테스트", "설명", 1L, 50, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
	}
}
