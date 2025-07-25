package com.example.surveyapi.domain.user.application;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UpdateRequest;
import com.example.surveyapi.domain.user.application.dto.request.WithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.GradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserResponse;
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

        LoginResponse.MemberResponse member = LoginResponse.MemberResponse.from(user);

        String token = jwtUtil.createToken(user.getId(), user.getRole());

        return LoginResponse.of(token, member);
    }

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(Pageable pageable) {

        Page<User> users = userRepository.gets(pageable);

        return users.map(UserResponse::from);
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

        UpdateRequest.UpdateData data = UpdateRequest.UpdateData.from(request);

        user.update(
            data.getPassword(),data.getName(),
            data.getProvince(),data.getDistrict(),
            data.getDetailAddress(),data.getPostalCode());

        return UserResponse.from(user);
    }


    @Transactional
    public void withdraw(Long userId, WithdrawRequest request) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        user.delete();
    }
}
