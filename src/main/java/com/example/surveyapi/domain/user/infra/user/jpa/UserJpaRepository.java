package com.example.surveyapi.domain.user.infra.user.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.surveyapi.domain.user.domain.user.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByAuthEmail(String email);

    Optional<User> findByAuthEmail(String authEmail);

    Optional<User> findByIdAndIsDeletedFalse(Long id);


}
