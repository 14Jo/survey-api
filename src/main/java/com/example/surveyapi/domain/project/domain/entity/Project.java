package com.example.surveyapi.domain.project.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.surveyapi.domain.project.domain.vo.ProjectPeriod;
import com.example.surveyapi.domain.project.domain.vo.ProjectState;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "projects")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(columnDefinition = "TEXT", nullable = false)
	private String description;

	@Column(nullable = false)
	private Long ownerId;

	@Embedded
	private ProjectPeriod period;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ProjectState state = ProjectState.PENDING;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private Boolean isDeleted = false;

	public static Project create(String name, String description, Long ownerId, ProjectPeriod period) {
		Project project = new Project();
		project.name = name;
		project.description = description;
		project.ownerId = ownerId;
		project.period = period;
		return project;
	}
}
