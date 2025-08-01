package com.example.surveyapi.domain.user.application;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.client.ParticipationPort;
import com.example.surveyapi.domain.user.application.client.ProjectPort;
import com.example.surveyapi.domain.user.application.dto.request.UpdateUserRequest;
import com.example.surveyapi.domain.user.application.dto.response.UpdateUserResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserGradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserInfoResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserSnapShotResponse;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Page<UserInfoResponse> getAll(Pageable pageable) {

        Page<User> users = userRepository.gets(pageable);

        return users.map(UserInfoResponse::from);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUser(Long userId) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return UserInfoResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserGradeResponse getGrade(Long userId) {

        Grade grade = userRepository.findByGrade(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.GRADE_NOT_FOUND));

        return UserGradeResponse.from(grade);
    }

    @Transactional
    public UpdateUserResponse update(UpdateUserRequest request, Long userId) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        String encryptedPassword = Optional.ofNullable(request.getAuth().getPassword())
            .map(passwordEncoder::encode)
            .orElse(null);

        UpdateUserRequest.UpdateData data = UpdateUserRequest.UpdateData.of(request, encryptedPassword);

        user.update(
            data.getPassword(), data.getName(),
            data.getProvince(), data.getDistrict(),
            data.getDetailAddress(), data.getPostalCode());

        return UpdateUserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserSnapShotResponse snapshot(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return UserSnapShotResponse.from(user);
    }
}
