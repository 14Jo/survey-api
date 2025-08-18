package com.example.surveyapi.domain.project.domain.project.event;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectMemberAddedDomainEvent {
    private final Long userId;
    private final LocalDateTime periodEnd;
    private final Long projectOwnerId;
    private final Long projectId;
}
