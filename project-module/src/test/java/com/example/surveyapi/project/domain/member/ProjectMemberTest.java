package com.example.surveyapi.project.domain.member;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import com.example.surveyapi.project.domain.project.entity.Project;
import com.example.surveyapi.global.exception.CustomException;

public class ProjectMemberTest {
	@Test
	void 멤버_추가_성공() {
		// given
		Project project = createProject();

		// when
		project.addMember(10L);

		// then
		assertThat(project.getProjectMembers()).hasSize(1);
		assertThat(project.getCurrentMemberCount()).isEqualTo(1);
		assertThat(project.getProjectMembers().get(0).getUserId()).isEqualTo(10L);
	}

	@Test
	void 이미_등록된_멤버_추가_예외() {
		// given
		Project project = createProject();
		project.addMember(10L);

		// when & then
		assertThatThrownBy(() -> project.addMember(10L))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("이미 등록된 인원");
	}

	@Test
	void 최대_인원수_초과_예외() {
		// given
		Project project = createProject();
		project.addMember(10L);
		project.addMember(11L);
		project.addMember(12L);

		// when & then
		assertThatThrownBy(() -> project.addMember(13L))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("프로젝트 최대 인원수를 초과하였습니다.");
	}

	@Test
	void 멤버_탈퇴_성공() {
		// given
		Project project = createProject();
		project.addMember(10L);

		// when
		project.removeMember(10L);

		// then
		assertThat(project.getCurrentMemberCount()).isEqualTo(0);
		assertThat(project.getProjectMembers().get(0).getIsDeleted()).isTrue();
	}

	private Project createProject() {
		return Project.create("테스트", "설명", 1L, 3, LocalDateTime.now(), LocalDateTime.now().plusDays(5));
	}
}
