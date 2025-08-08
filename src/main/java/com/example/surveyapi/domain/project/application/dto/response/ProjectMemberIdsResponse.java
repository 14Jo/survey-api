package com.example.surveyapi.domain.project.application.dto.response;

import java.util.List;

import com.example.surveyapi.domain.project.domain.participant.member.entity.ProjectMember;
import com.example.surveyapi.domain.project.domain.project.entity.Project;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectMemberIdsResponse {
	private int currentMemberCount; // 현재 인원수
	private int maxMembers;         // 최대 인원수
	private List<Long> memberIds;   // 참여한 유저 id 리스트

	public static ProjectMemberIdsResponse from(Project project) {
		List<Long> ids = project.getProjectMembers().stream()
			.map(ProjectMember::getUserId)
			.toList();

		ProjectMemberIdsResponse response = new ProjectMemberIdsResponse();
		response.currentMemberCount = ids.size();
		response.maxMembers = project.getMaxMembers();
		response.memberIds = ids;

		return response;
	}
}
