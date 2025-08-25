package com.example.surveyapi.user.domain.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.surveyapi.user.domain.auth.enums.Provider;
import com.example.surveyapi.user.domain.command.UserGradePoint;

public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByProfileNickName(String nickname);

    User save(User user);

    Optional<User> findByEmailAndIsDeletedFalse(String email);

    Page<User> gets(Pageable pageable);

    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    Optional<User> findById(Long userId);

    Optional<UserGradePoint> findByGradeAndPoint(Long userId);

    Optional<User> findByAuthProviderAndAuthProviderIdAndIsDeletedFalse(Provider provider, String providerId);

    Optional<Long> findIdByAuthEmail(String email);
}
