package com.example.surveyapi.domain.user.infra.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.domain.user.domain.command.UserGradePoint;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.domain.user.infra.user.dsl.QueryDslRepository;
import com.example.surveyapi.domain.user.infra.user.jpa.UserJpaRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final QueryDslRepository queryDslRepository;

    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByAuthEmail(email);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findByEmailAndIsDeletedFalse(String email) {
        return userJpaRepository.findByAuthEmailAndIsDeletedFalse(email);
    }

    @Override
    public Page<User> gets(Pageable pageable) {
        return queryDslRepository.gets(pageable);
    }

    @Override
    public Optional<User> findByIdAndIsDeletedFalse(Long memberId) {
        return userJpaRepository.findByIdAndIsDeletedFalse(memberId);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public Optional<UserGradePoint> findByGradeAndPoint(Long userId) {
        return userJpaRepository.findByGradeAndPoint(userId);
    }

    @Override
    public Optional<User> findByAuthProviderIdAndIsDeletedFalse(String providerId) {
        return userJpaRepository.findByAuthProviderIdAndIsDeletedFalse(providerId);
    }

}
