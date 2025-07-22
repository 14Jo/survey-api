package com.example.surveyapi.domain.project.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.surveyapi.domain.project.domain.vo.ManagerRole;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "managers")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Manager {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id", nullable = false)
	private Project project;

	@Column(nullable = false)
	private Long memberId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ManagerRole role = ManagerRole.READ;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private Boolean isDeleted = false;

	public static Manager createOwner(Project project, Long memberId) {
		Manager manager = new Manager();
		manager.project = project;
		manager.memberId = memberId;
		manager.role = ManagerRole.OWNER;
		return manager;
	}
}
