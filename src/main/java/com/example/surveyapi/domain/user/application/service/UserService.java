package com.example.surveyapi.domain.user.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.config.jwt.JwtUtil;
import com.example.surveyapi.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.application.dtos.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dtos.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dtos.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dtos.response.MemberResponse;
import com.example.surveyapi.domain.user.application.dtos.response.SignupResponse;
import com.example.surveyapi.domain.user.application.dtos.response.UserListResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Auth;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;
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

        User user = from(request, passwordEncoder);

        User createUser = userRepository.save(user);

        return new SignupResponse(createUser);
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

        return new LoginResponse(token, member);
    }

    @Transactional
    public UserListResponse getAll(){

    }


    public static User from(SignupRequest request, PasswordEncoder passwordEncoder) {
        Address address = new Address(
            request.getProfile().getAddress().getProvince(),
            request.getProfile().getAddress().getDistrict(),
            request.getProfile().getAddress().getDetailAddress(),
            request.getProfile().getAddress().getPostalCode()
        );

        Profile profile = new Profile(
            request.getProfile().getName(),
            request.getProfile().getBirthDate(),
            request.getProfile().getGender(),
            address);

        Auth auth = new Auth(
            request.getAuth().getEmail(),
            passwordEncoder.encode(request.getAuth().getPassword()));

        return new User(auth, profile);
    }
}
