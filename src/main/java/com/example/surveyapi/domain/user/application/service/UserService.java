package com.example.surveyapi.domain.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.dtos.request.SignupRequest;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.application.dtos.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dtos.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dtos.response.MemberResponse;
import com.example.surveyapi.domain.user.application.dtos.response.SignupResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.domain.user.domain.user.command.SignupCommand;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // Todo Command로 변경될 경우 사용할 메서드
    // @Transactional
    // public SignupResponse signup(SignupRequest request) {
    //
    //     SignupCommand command = request.toCommand();
    //
    //     if (userRepository.existsByEmail(command.getAuth().getEmail())) {
    //         throw new CustomException(CustomErrorCode.EMAIL_NOT_FOUND);
    //     }
    //
    //     User user = User.create(command, passwordEncoder);
    //
    //     User createUser = userRepository.save(user);
    //
    //     return SignupResponse.from(createUser);
    // }

    @Transactional
    public SignupResponse signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getAuth().getEmail())) {
            throw new CustomException(CustomErrorCode.EMAIL_NOT_FOUND);
        }

        User user = User.from(
            request.getAuth().getEmail(),
            request.getAuth().getPassword(),
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


}
