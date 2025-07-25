package com.example.surveyapi.domain.user.infra.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

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
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByAuthEmail(email);
    }

    @Override
    public Page<User> gets(Pageable pageable) {
        return queryDslRepository.gets(pageable);
    }

    @Override
    public Optional<User> findByIdAndIsDeletedFalse(Long memberId) {
        return userJpaRepository.findByIdAndIsDeletedFalse(memberId);
    }
}
