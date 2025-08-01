package com.example.surveyapi.domain.user.domain.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.domain.user.domain.user.enums.Grade;

public interface UserRepository {

    boolean existsByEmail(String email);

    User save(User user);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Page<User> gets(Pageable pageable);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    Optional<Grade> findByGrade(Long userId);

    boolean existsByAuthProviderId(String providerId);

    Optional<User> findByAuthProviderIdAndIsDeletedFalse(String providerId);
}
