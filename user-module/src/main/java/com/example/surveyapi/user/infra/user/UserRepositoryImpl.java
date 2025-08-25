package com.example.surveyapi.user.infra.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.example.surveyapi.user.domain.auth.enums.Provider;
import com.example.surveyapi.user.domain.command.UserGradePoint;
import com.example.surveyapi.user.domain.user.User;
import com.example.surveyapi.user.domain.user.UserRepository;
import com.example.surveyapi.user.infra.user.dsl.QueryDslRepository;
import com.example.surveyapi.user.infra.user.jpa.UserJpaRepository;

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
    public boolean existsByProfileNickName(String nickname) {
        return userJpaRepository.existsByProfileNickName(nickname);
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
    public Optional<User> findByAuthProviderAndAuthProviderIdAndIsDeletedFalse(Provider provider, String providerId) {
        return userJpaRepository.findByAuthProviderAndAuthProviderIdAndIsDeletedFalse(provider, providerId);
    }

    @Override
    public Optional<Long> findIdByAuthEmail(String email) {
        return userJpaRepository.findIdByAuthEmail(email);
    }

}
