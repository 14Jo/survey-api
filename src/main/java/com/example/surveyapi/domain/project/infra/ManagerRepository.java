package com.example.surveyapi.domain.project.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.project.domain.entity.Manager;

public interface ManagerRepository extends JpaRepository<Manager, Long> {
}
