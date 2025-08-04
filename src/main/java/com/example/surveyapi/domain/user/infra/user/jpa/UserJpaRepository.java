package com.example.surveyapi.domain.user.infra.user.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.user.domain.command.UserGradePoint;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByAuthEmail(String email);

    Optional<User> findByAuthEmailAndIsDeletedFalse(String authEmail);

    Optional<User> findByIdAndIsDeletedFalse(Long id);

    Optional<User> findById(Long id);

    @Query("SELECT u.grade, u.point FROM User u WHERE u.id = :userId")
    Optional<UserGradePoint> findByGradeAndPoint(@Param("userId") Long userId);

    Optional<User> findByAuthProviderIdAndIsDeletedFalse(String authProviderId);

}
