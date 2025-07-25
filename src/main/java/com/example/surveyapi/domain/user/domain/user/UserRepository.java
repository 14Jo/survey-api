package com.example.surveyapi.domain.user.domain.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import com.example.surveyapi.domain.user.application.dto.response.UserResponse;

public interface UserRepository {

    boolean existsByEmail(String email);

    User save(User user);

    Optional<User> findByEmail(String email);

    Page<User> gets(Pageable pageable);

    Optional<User> findByIdAndIsDeletedFalse(Long memberId);
}
