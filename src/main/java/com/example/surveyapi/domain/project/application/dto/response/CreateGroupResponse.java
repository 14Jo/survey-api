package com.example.surveyapi.domain.project.application.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CreateGroupResponse {
	private Long groupId;
	private String groupName;

	public static CreateGroupResponse of(Long groupId, String groupName) {
		return new CreateGroupResponse(groupId, groupName);
	}
}
