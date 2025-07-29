package com.example.surveyapi.domain.project.domain.group.entity;

import com.example.surveyapi.domain.project.domain.group.enums.AgeGroup;
import com.example.surveyapi.domain.project.domain.project.entity.Project;
import com.example.surveyapi.global.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "groups")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Group extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private AgeGroup ageGroup;

	public static Group create(Project project, AgeGroup ageGroup) {
		Group group = new Group();
		group.project = project;
		group.ageGroup = ageGroup;

		return group;
	}
}
