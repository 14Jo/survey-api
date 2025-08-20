package com.example.surveyapi.domain.user.application;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.client.port.OAuthPort;
import com.example.surveyapi.domain.user.application.client.request.GoogleOAuthRequest;
import com.example.surveyapi.domain.user.application.client.request.KakaoOAuthRequest;
import com.example.surveyapi.domain.user.application.client.request.NaverOAuthRequest;
import com.example.surveyapi.domain.user.application.client.response.GoogleAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.GoogleUserInfoResponse;
import com.example.surveyapi.domain.user.application.client.response.KakaoAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.KakaoUserInfoResponse;
import com.example.surveyapi.domain.user.application.client.response.NaverAccessResponse;
import com.example.surveyapi.domain.user.application.client.response.NaverUserInfoResponse;
import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.domain.user.domain.auth.enums.Provider;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRedisRepository;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.oauth.GoogleOAuthProperties;
import com.example.surveyapi.global.config.oauth.KakaoOAuthProperties;
import com.example.surveyapi.global.config.oauth.NaverOAuthProperties;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OAuthPort OAuthPort;
    private final KakaoOAuthProperties kakaoOAuthProperties;
    private final NaverOAuthProperties naverOAuthProperties;
    private final GoogleOAuthProperties googleOAuthProperties;
    private final UserRedisRepository userRedisRepository;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        User createUser = createAndSaveUser(request);

        return SignupResponse.from(createUser);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailAndIsDeletedFalse(request.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        return createAccessAndSaveRefresh(user);
    }

    @Transactional
    public void withdraw(Long userId, UserWithdrawRequest request, String authHeader) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        user.delete();
        user.registerUserWithdrawEvent();
        userRepository.withdrawSave(user);

        String accessToken = jwtUtil.subStringToken(authHeader);

        validateTokenType(accessToken, "access");

        addBlackLists(accessToken);

        userRedisRepository.delete(userId);
    }

    @Transactional
    public void logout(String authHeader, Long userId) {

        String accessToken = jwtUtil.subStringToken(authHeader);

        validateTokenType(accessToken, "access");
        addBlackLists(accessToken);

        userRedisRepository.delete(userId);
    }

    @Transactional
    public LoginResponse reissue(String authHeader, String bearerRefreshToken) {
        String accessToken = jwtUtil.subStringToken(authHeader);
        String refreshToken = jwtUtil.subStringToken(bearerRefreshToken);

        Claims refreshClaims = jwtUtil.extractClaims(refreshToken);

        validateTokenType(accessToken, "access");
        validateTokenType(refreshToken, "refresh");

        String blackListKey = "blackListToken" + accessToken;
        String saveBlackListKey = userRedisRepository.getRedisKey(blackListKey);

        if (saveBlackListKey != null) {
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
        String storedBearerRefreshToken = userRedisRepository.getRedisKey(redisKey);

        if (storedBearerRefreshToken == null) {
            throw new CustomException(CustomErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        if (!refreshToken.equals(jwtUtil.subStringToken(storedBearerRefreshToken))) {
            throw new CustomException(CustomErrorCode.MISMATCH_REFRESH_TOKEN);
        }

        userRedisRepository.delete(userId);

        return createAccessAndSaveRefresh(user);
    }

    @Transactional
    public LoginResponse kakaoLogin(String code, SignupRequest request) {
        log.info("카카오 로그인 실행");
        // 인가 코드 → 액세스 토큰
        KakaoAccessResponse kakaoAccessToken = getKakaoAccessToken(code);
        log.info("액세스 토큰 발급 완료");

        // 액세시 토큰 → 사용자 정보 (providerId)
        KakaoUserInfoResponse kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken.getAccess_token());
        log.info("providerId 획득");

        String providerId = String.valueOf(kakaoUserInfo.getProviderId());

        // 회원가입 유저인지 확인
        User user = userRepository.findByAuthProviderAndAuthProviderIdAndIsDeletedFalse(Provider.KAKAO, providerId)
            .orElseGet(() -> {
                User newUser = createAndSaveUser(request);
                newUser.getAuth().updateProviderId(providerId);
                log.info("회원가입 완료");
                return newUser;
            });

        return createAccessAndSaveRefresh(user);
    }

    @Transactional
    public LoginResponse naverLogin(String code, SignupRequest request) {
        log.info("네이버 로그인 실행");
        NaverAccessResponse naverAccessToken = getNaverAcessToken(code);
        log.info("액세스 토큰 발급 완료");

        NaverUserInfoResponse naverUserInfo = getNaverUserInfo(naverAccessToken.getAccess_token());
        log.info("providerId 획득");

        String providerId = String.valueOf(naverUserInfo.getResponse().getProviderId());

        // 회원가입 유저인지 확인
        User user = userRepository.findByAuthProviderAndAuthProviderIdAndIsDeletedFalse(Provider.NAVER, providerId)
            .orElseGet(() -> {
                User newUser = createAndSaveUser(request);
                newUser.getAuth().updateProviderId(providerId);
                log.info("회원가입 완료");
                return newUser;
            });

        return createAccessAndSaveRefresh(user);
    }

    @Transactional
    public LoginResponse googleLogin(String code, SignupRequest request) {
        log.info("구글 로그인 실행");
        GoogleAccessResponse googleAccessResponse = getGoogleAccessToken(code);
        log.info("액세스 토큰 발급 완료");

        GoogleUserInfoResponse googleuserInfo = getGoogleUserInfo(googleAccessResponse.getAccess_token());
        log.info("providerId 획득");

        String providerId = String.valueOf(googleuserInfo.getProviderId());

        // 회원가입 유저인지 확인
        User user = userRepository.findByAuthProviderAndAuthProviderIdAndIsDeletedFalse(Provider.GOOGLE, providerId)
            .orElseGet(() -> {
                User newUser = createAndSaveUser(request);
                newUser.getAuth().updateProviderId(providerId);
                log.info("회원가입 완료");
                return newUser;
            });

        return createAccessAndSaveRefresh(user);

    }

    private User createAndSaveUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getAuth().getEmail())) {
            throw new CustomException(CustomErrorCode.EMAIL_DUPLICATED);
        }

        if (userRepository.existsByProfileNickName(request.getProfile().getNickName())) {
            throw new CustomException(CustomErrorCode.NICKNAME_DUPLICATED);
        }

        String encryptedPassword = passwordEncoder.encode(request.getAuth().getPassword());

        User user = User.create(
            request.getAuth().getEmail(),
            encryptedPassword,
            request.getProfile().getName(),
            request.getProfile().getPhoneNumber(),
            request.getProfile().getNickName(),
            request.getProfile().getBirthDate(),
            request.getProfile().getGender(),
            request.getProfile().getAddress().getProvince(),
            request.getProfile().getAddress().getDistrict(),
            request.getProfile().getAddress().getDetailAddress(),
            request.getProfile().getAddress().getPostalCode(),
            request.getAuth().getProvider()
        );

        return userRepository.save(user);
    }

    private LoginResponse createAccessAndSaveRefresh(User user) {

        String newAccessToken = jwtUtil.createAccessToken(user.getId(), user.getRole());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getId(), user.getRole());

        String redisKey = "refreshToken" + user.getId();
        userRedisRepository.saveRedisKey(redisKey, newRefreshToken, Duration.ofDays(7));

        return LoginResponse.of(newAccessToken, newRefreshToken, user);
    }

    private void addBlackLists(String accessToken) {

        Long remainingTime = jwtUtil.getExpiration(accessToken);
        String blackListTokenKey = "blackListToken" + accessToken;

        userRedisRepository.saveRedisKey(blackListTokenKey, "logout", Duration.ofMillis(remainingTime));
    }

    private void validateTokenType(String token, String expectedType) {
        String type = jwtUtil.extractClaims(token).get("type", String.class);
        if (!expectedType.equals(type)) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN_TYPE);
        }
    }

    private KakaoAccessResponse getKakaoAccessToken(String code) {

        try {
            KakaoOAuthRequest request = KakaoOAuthRequest.of(
                "authorization_code",
                kakaoOAuthProperties.getClientId(),
                kakaoOAuthProperties.getRedirectUri(),
                code);

            return OAuthPort.getKakaoAccess(request);
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.OAUTH_ACCESS_TOKEN_FAILED);
        }
    }

    private KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        try {
            return OAuthPort.getKakaoUserInfo("Bearer " + accessToken);
        } catch (Exception e) {
            log.error("카카오 사용자 정보 조회 실패, accessToken: {}", accessToken, e);
            throw new CustomException(CustomErrorCode.PROVIDER_ID_NOT_FOUNT);
        }
    }

    private NaverAccessResponse getNaverAcessToken(String code) {
        try {
            NaverOAuthRequest request = NaverOAuthRequest.of(
                "authorization_code",
                naverOAuthProperties.getClientId(),
                naverOAuthProperties.getClientSecret(),
                code,
                "test_state_123");

            return OAuthPort.getNaverAccess(request);
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.OAUTH_ACCESS_TOKEN_FAILED);
        }
    }

    private NaverUserInfoResponse getNaverUserInfo(String accessToken) {
        try {
            return OAuthPort.getNaverUserInfo("Bearer " + accessToken);
        } catch (Exception e) {
            log.error("네이버 사용자 정보 조회 실패, accessToken: {}", accessToken, e);
            throw new CustomException(CustomErrorCode.PROVIDER_ID_NOT_FOUNT);
        }
    }

    private GoogleAccessResponse getGoogleAccessToken(String code) {
        try {
            GoogleOAuthRequest request = GoogleOAuthRequest.of(
                "authorization_code",
                googleOAuthProperties.getClientId(),
                googleOAuthProperties.getClientSecret(),
                googleOAuthProperties.getRedirectUri(),
                code);

            return OAuthPort.getGoogleAccess(request);
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.OAUTH_ACCESS_TOKEN_FAILED);
        }
    }

    private GoogleUserInfoResponse getGoogleUserInfo(String accessToken) {
        try {

            return OAuthPort.getGoogleUserInfo("Bearer " + accessToken);
        } catch (Exception e) {
            log.error("구글 사용자 정보 조회 실패, accessToken: {}", accessToken, e);
            throw new CustomException(CustomErrorCode.PROVIDER_ID_NOT_FOUNT);
        }
    }
}
