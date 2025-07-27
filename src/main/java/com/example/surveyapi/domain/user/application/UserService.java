package com.example.surveyapi.domain.user.application;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UpdateUserRequest;
import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.UpdateUserResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserGradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserInfoResponse;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Auth;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getAuth().getEmail())) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_FOUND);
        }

        String encryptedPassword = passwordEncoder.encode(request.getAuth().getPassword());

        Address address = Address.of(
            request.getProfile().getAddress().getProvince(),
            request.getProfile().getAddress().getDistrict(),
            request.getProfile().getAddress().getDetailAddress(),
            request.getProfile().getAddress().getPostalCode());

        Profile profile = Profile.of(
            request.getProfile().getName(),
            request.getProfile().getBirthDate(),
            request.getProfile().getGender(),
            address
        );

        Auth auth = Auth.of(request.getAuth().getEmail(), encryptedPassword);

        User user = User.create(auth, profile);

        User createUser = userRepository.save(user);

        return SignupResponse.from(createUser);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        String token = jwtUtil.createToken(user.getId(), user.getRole());

        return LoginResponse.of(token, user);
    }

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

    @Transactional
    public void withdraw(Long userId, UserWithdrawRequest request) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        user.delete();
    }
}
