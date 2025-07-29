package com.example.surveyapi.domain.user.application;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UpdateUserRequest;
import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.UpdateUserResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserGradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserInfoResponse;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

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
            request.getProfile().getAddress().getPostalCode()
        );

        User createUser = userRepository.save(user);

        user.getAuth().updateProviderId(createUser.getId().toString());

        return SignupResponse.from(createUser);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        return createAccessAndSaveRefresh(user);
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

    @Transactional
    public void logout(String bearerAccessToken, Long userId) {

        String accessToken = jwtUtil.subStringToken(bearerAccessToken);

        validateTokenType(accessToken, "access");

        addBlackLists(accessToken);

        String redisKey = "refreshToken" + userId;
        redisTemplate.delete(redisKey);
    }

    public LoginResponse reissue(String bearerAccessToken, String bearerRefreshToken) {
        String accessToken = jwtUtil.subStringToken(bearerAccessToken);
        String refreshToken = jwtUtil.subStringToken(bearerRefreshToken);

        Claims refreshClaims = jwtUtil.extractClaims(refreshToken);

        validateTokenType(accessToken, "access");
        validateTokenType(refreshToken, "refresh");

        if (redisTemplate.opsForValue().get("blackListToken" + accessToken) != null) {
            throw new CustomException(CustomErrorCode.BLACKLISTED_TOKEN);
        }

        if (jwtUtil.isTokenExpired(accessToken)) {
            throw new CustomException(CustomErrorCode.ACCESS_TOKEN_NOT_EXPIRED);
        }

        jwtUtil.validateToken(refreshToken);

        long userId = Long.parseLong(refreshClaims.getSubject());
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        String redisKey = "refreshToken" + userId;
        String storedBearerRefreshToken = redisTemplate.opsForValue().get(redisKey);

        if (storedBearerRefreshToken == null) {
            throw new CustomException(CustomErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        if (!refreshToken.equals(jwtUtil.subStringToken(storedBearerRefreshToken))) {
            throw new CustomException(CustomErrorCode.MISMATCH_REFRESH_TOKEN);
        }

        redisTemplate.delete(redisKey);

        return createAccessAndSaveRefresh(user);
    }

    private LoginResponse createAccessAndSaveRefresh(User user) {

        String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getId(), user.getRole());

        String redisKey = "refreshToken" + user.getId();
        redisTemplate.opsForValue().set(redisKey, newRefreshToken, Duration.ofDays(7));

        return LoginResponse.of(newAccessToken, newRefreshToken, user);
    }

    private void addBlackLists(String accessToken) {

        Long remainingTime = jwtUtil.getExpiration(accessToken);
        String blackListTokenKey = "blackListToken" + accessToken;

        redisTemplate.opsForValue().set(blackListTokenKey, "logout", Duration.ofMillis(remainingTime));
    }

    private void validateTokenType(String token, String expectedType) {
        String type = jwtUtil.extractClaims(token).get("type", String.class);
        if (!expectedType.equals(type)) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN_TYPE);
        }
    }
}
