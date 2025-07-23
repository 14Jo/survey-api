package com.example.surveyapi.domain.user.application.service;

import static com.example.surveyapi.domain.user.application.dtos.request.vo.update.UpdateData.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.dtos.request.auth.SignupRequest;
import com.example.surveyapi.domain.user.application.dtos.request.UpdateRequest;
import com.example.surveyapi.domain.user.application.dtos.request.vo.update.UpdateData;
import com.example.surveyapi.domain.user.application.dtos.response.select.GradeResponse;
import com.example.surveyapi.domain.user.application.dtos.response.UserResponse;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.application.dtos.request.auth.LoginRequest;
import com.example.surveyapi.domain.user.application.dtos.response.auth.LoginResponse;
import com.example.surveyapi.domain.user.application.dtos.response.auth.MemberResponse;
import com.example.surveyapi.domain.user.application.dtos.response.auth.SignupResponse;
import com.example.surveyapi.domain.user.application.dtos.response.select.UserListResponse;
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

        User user = User.create(
            request.getAuth().getEmail(),
            encryptedPassword,
            request.getProfile().getName(),
            request.getProfile().getBirthDate(),
            request.getProfile().getGender(),
            request.getProfile().getAddress().getProvince(),
            request.getProfile().getAddress().getDistrict(),
            request.getProfile().getAddress().getDetailAddress(),
            request.getProfile().getAddress().getPostalCode());

        User createUser = userRepository.save(user);

        return SignupResponse.from(createUser);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        MemberResponse member = MemberResponse.from(user);

        String token = jwtUtil.createToken(user.getId(), user.getRole());

        return LoginResponse.of(token, member);
    }

    @Transactional(readOnly = true)
    public UserListResponse getAll(Pageable pageable) {

        Page<UserResponse> users = userRepository.gets(pageable);

        return UserListResponse.from(users);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long memberId) {

        User user = userRepository.findByIdAndIsDeletedFalse(memberId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public GradeResponse getGrade(Long userId) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        return GradeResponse.from(user);
    }

    @Transactional
    public UserResponse update(UpdateRequest request, Long userId){

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        UpdateData data = extractUpdateData(request);

        user.update(
            data.getPassword(),data.getName(),
            data.getProvince(),data.getDistrict(),
            data.getDetailAddress(),data.getPostalCode());

        return UserResponse.from(user);
    }
}
