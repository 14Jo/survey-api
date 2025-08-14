package com.example.surveyapi.domain.user.infra.user.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.surveyapi.domain.user.domain.auth.enums.Provider;
import com.example.surveyapi.domain.user.domain.command.UserGradePoint;
import com.example.surveyapi.domain.user.domain.user.User;

public interface UserJpaRepository extends JpaRepository<User, Long> {

    boolean existsByAuthEmail(String email);

    boolean existsByProfileNickName(String nickname);

    @Query("SELECT u FROM User u join fetch u.auth a join fetch u.demographics d WHERE a.email = :authEmail AND a.isDeleted = false")
    Optional<User> findByAuthEmailAndIsDeletedFalse(@Param("authEmail") String authEmail);

    @Query("SELECT u FROM User u join fetch u.auth a join fetch u.demographics d WHERE u.id = :userId AND u.isDeleted = false")
    Optional<User> findByIdAndIsDeletedFalse(Long userId);

    Optional<User> findById(Long usreId);

    @Query("SELECT u.grade, u.point FROM User u WHERE u.id = :userId")
    Optional<UserGradePoint> findByGradeAndPoint(@Param("userId") Long userId);

    Optional<User> findByAuthProviderAndAuthProviderIdAndIsDeletedFalse(Provider provider, String authProviderId);

}
