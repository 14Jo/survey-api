package com.example.surveyapi.domain.user.application;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.surveyapi.domain.user.application.client.KakaoOauthPort;
import com.example.surveyapi.domain.user.application.client.KakaoOauthRequest;
import com.example.surveyapi.domain.user.application.client.KakaoAccessResponse;
import com.example.surveyapi.domain.user.application.client.KakaoUserInfoResponse;
import com.example.surveyapi.domain.user.application.client.MyProjectRoleResponse;
import com.example.surveyapi.domain.user.application.client.ParticipationPort;
import com.example.surveyapi.domain.user.application.client.ProjectPort;
import com.example.surveyapi.domain.user.application.client.UserSurveyStatusResponse;
import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.oauth.KakaoOauthProperties;
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
    private final RedisTemplate<String, String> redisTemplate;
    private final ProjectPort projectPort;
    private final ParticipationPort participationPort;
    private final KakaoOauthPort kakaoOauthPort;
    private final KakaoOauthProperties kakaoOauthProperties;

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

    // Todo 회원 탈퇴 시 이벤트 -> @UserWithdraw 어노테이션을 붙이기만 하면 됩니다.
    @Transactional
    public void withdraw(Long userId, UserWithdrawRequest request, String authHeader) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getAuth().getPassword())) {
            throw new CustomException(CustomErrorCode.WRONG_PASSWORD);
        }

        List<MyProjectRoleResponse> myRoleList = projectPort.getProjectMyRole(authHeader, userId);
        log.info("프로젝트 조회 성공 : {}", myRoleList.size() );

        for (MyProjectRoleResponse myRole : myRoleList) {
            log.info("권한 : {}", myRole.getMyRole());
            if ("OWNER".equals(myRole.getMyRole())) {
                throw new CustomException(CustomErrorCode.PROJECT_ROLE_OWNER);
            }
        }

        int page = 0;
        int size = 20;

        List<UserSurveyStatusResponse> surveyStatus =
            participationPort.getParticipationSurveyStatus(authHeader, userId, page, size);
        log.info("설문 참여 상태 수: {}", surveyStatus.size());

        for (UserSurveyStatusResponse survey : surveyStatus) {
            log.info("설문 상태: {}", survey.getSurveyStatus());
            if ("IN_PROGRESS".equals(survey.getSurveyStatus())) {
                throw new CustomException(CustomErrorCode.SURVEY_IN_PROGRESS);
            }
        }

        user.delete();

        // 상위 트랜잭션이 유지됨
        logout(authHeader,userId);
    }

    @Transactional
    public void logout(String authHeader, Long userId) {

        String accessToken = jwtUtil.subStringToken(authHeader);

        validateTokenType(accessToken, "access");

        addBlackLists(accessToken);

        String redisKey = "refreshToken" + userId;
        redisTemplate.delete(redisKey);
    }

    @Transactional
    public LoginResponse reissue(String authHeader, String bearerRefreshToken) {
        String accessToken = jwtUtil.subStringToken(authHeader);
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

    @Transactional
    public LoginResponse kakaoLogin(
       String code, SignupRequest request
    ){
        log.info("카카오 로그인 실행");
        // 인가 코드 → 액세스 토큰
        KakaoAccessResponse kakaoAccessToken = getKakaoAccessToken(code);
        log.info("액세스 토큰 발급 완료");

        // 액세시 토큰 → 사용자 정보 (providerId)
        KakaoUserInfoResponse kakaoUserInfo = getKakaoUserInfo(kakaoAccessToken.getAccess_token());
        log.info("providerId 획득");

        String providerId = String.valueOf(kakaoUserInfo.getProviderId());

        // 회원가입 유저인지 확인
        User user = userRepository.findByAuthProviderIdAndIsDeletedFalse(providerId)
            .orElseGet(() ->  {
                    User newUser = createAndSaveUser(request);
                    log.info("회원가입 완료");
                    return newUser;
                });

        return createAccessAndSaveRefresh(user);
    }

    private User createAndSaveUser(SignupRequest request) {
        if (userRepository.existsByEmail(request.getAuth().getEmail())) {
            throw new CustomException(CustomErrorCode.EMAIL_DUPLICATED);
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

        return createUser;
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

    private KakaoAccessResponse getKakaoAccessToken(String code){

        try {
            KakaoOauthRequest request = KakaoOauthRequest.of(
                "authorization_code",
                kakaoOauthProperties.getClientId(),
                kakaoOauthProperties.getRedirectUri(),
                code);

            return kakaoOauthPort.getKakaoAccess(request);
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.OAUTH_ACCESS_TOKEN_FAILED);
        }
    }

    private KakaoUserInfoResponse getKakaoUserInfo(String accessToken){
        try {
            return kakaoOauthPort.getKakaoUserInfo("Bearer " + accessToken);
        } catch (Exception e) {
            // throw new CustomException(CustomErrorCode.PROVIDER_ID_NOT_FOUNT);
            log.error("카카오 사용자 정보 요청 실패 : " , e);
            throw new RuntimeException("오류발생 : " + e.getMessage());
        }
    }
}
