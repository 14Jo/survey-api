package com.example.surveyapi.domain.user.domain.user;

import java.util.Optional;

public interface UserRepository {

    boolean existsByEmail(String email);
    User save(User user);
    Optional<User> findByEmail(String email);
}
