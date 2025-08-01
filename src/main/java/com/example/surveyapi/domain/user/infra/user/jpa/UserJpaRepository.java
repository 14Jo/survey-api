package com.example.surveyapi.domain.user.infra.user.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByAuthEmail(String email);

    Optional<User> findByAuthEmailAndIsDeletedFalse(String authEmail);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT u.grade FROM User u WHERE u.id = :userId")
    Optional<Grade> findByGrade(@Param("userId") Long userId);

    boolean existsByAuthProviderId(String authProviderId);

    Optional<User> findByAuthProviderIdAndIsDeletedFalse(String authProviderId);

}
