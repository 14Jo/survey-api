package com.example.surveyapi.user.application;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.user.application.dto.request.UpdateUserRequest;
import com.example.surveyapi.user.application.dto.response.UpdateUserResponse;
import com.example.surveyapi.user.application.dto.response.UserByEmailResponse;
import com.example.surveyapi.user.application.dto.response.UserGradeResponse;
import com.example.surveyapi.user.application.dto.response.UserInfoResponse;
import com.example.surveyapi.user.application.dto.response.UserSnapShotResponse;
import com.example.surveyapi.user.domain.command.UserGradePoint;
import com.example.surveyapi.global.auth.jwt.PasswordEncoder;
import com.example.surveyapi.user.domain.user.User;
import com.example.surveyapi.user.domain.user.UserRepository;
import com.example.surveyapi.global.exception.CustomErrorCode;
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
    public UserGradeResponse getGradeAndPoint(Long userId) {

        UserGradePoint userGradePoint = userRepository.findByGradeAndPoint(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.GRADE_POINT_NOT_FOUND));

        return UserGradeResponse.from(userGradePoint);
    }

    @Transactional
    public UpdateUserResponse update(UpdateUserRequest request, Long userId) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        String encryptedPassword = Optional.ofNullable(request.getPassword())
            .map(passwordEncoder::encode)
            .orElseGet(() -> user.getAuth().getPassword());

        UpdateUserRequest.UpdateData data = UpdateUserRequest.UpdateData.of(request, encryptedPassword);

        user.update(
            data.getPassword(), data.getName(),
            data.getPhoneNumber(), data.getNickName(),
            data.getProvince(), data.getDistrict(),
            data.getDetailAddress(), data.getPostalCode()
        );

        userRepository.save(user);

        return UpdateUserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserSnapShotResponse snapshot(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return UserSnapShotResponse.from(user);
    }

    public void updatePoint(Long userId) {
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        user.increasePoint();
        userRepository.save(user);
    }

    public UserByEmailResponse byEmail(String email){
        Long userId = userRepository.findIdByAuthEmail(email)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USERID_NOT_FOUND));

        return UserByEmailResponse.from(userId);
    }
}
