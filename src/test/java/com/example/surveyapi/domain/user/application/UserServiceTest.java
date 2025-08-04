package com.example.surveyapi.domain.user.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UpdateUserRequest;
import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.UpdateUserResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserGradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserInfoResponse;
import com.example.surveyapi.domain.user.domain.auth.enums.Provider;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.global.config.client.ExternalApiResponse;
import com.example.surveyapi.global.config.client.participation.ParticipationApiClient;
import com.example.surveyapi.global.config.client.project.ProjectApiClient;
import com.example.surveyapi.global.config.jwt.JwtUtil;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @MockitoBean
    private ProjectApiClient projectApiClient;

    @MockitoBean
    private ParticipationApiClient participationApiClient;

    @Test
    @DisplayName("회원 가입 - 성공 (DB 저장 검증)")
    void signup_success() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest request = createSignupRequest(email, password);

        // when
        SignupResponse signup = authService.signup(request);

        // then
        var savedUser = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail()).orElseThrow();
        assertThat(savedUser.getProfile().getName()).isEqualTo("홍길동");
        assertThat(savedUser.getDemographics().getAddress().getProvince()).isEqualTo("서울특별시");
    }

    @Test
    @DisplayName("회원 가입 - 실패 (auth 정보 누락)")
    void signup_fail_when_auth_is_null() throws Exception {
        // given
        SignupRequest request = new SignupRequest();
        SignupRequest.ProfileRequest profileRequest = new SignupRequest.ProfileRequest();
        SignupRequest.AddressRequest addressRequest = new SignupRequest.AddressRequest();

        ReflectionTestUtils.setField(addressRequest, "province", "서울특별시");
        ReflectionTestUtils.setField(addressRequest, "district", "강남구");
        ReflectionTestUtils.setField(addressRequest, "detailAddress", "테헤란로 123");
        ReflectionTestUtils.setField(addressRequest, "postalCode", "06134");

        ReflectionTestUtils.setField(profileRequest, "name", "홍길동");
        ReflectionTestUtils.setField(profileRequest, "birthDate", LocalDateTime.parse("1990-01-01T09:00:00"));
        ReflectionTestUtils.setField(profileRequest, "gender", Gender.MALE);
        ReflectionTestUtils.setField(profileRequest, "address", addressRequest);

        ReflectionTestUtils.setField(request, "profile", profileRequest);

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(
                result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }

    @Test
    @DisplayName("비밀번호 암호화 확인")
    void signup_passwordEncoder() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest request = createSignupRequest(email, password);

        // when
        SignupResponse signup = authService.signup(request);

        // then
        var savedUser = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail()).orElseThrow();
        assertThat(passwordEncoder.matches("Password123", savedUser.getAuth().getPassword())).isTrue();
    }

    @Test
    @DisplayName("응답 Dto 반영 확인")
    void signup_response() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest request = createSignupRequest(email, password);

        // when
        SignupResponse signup = authService.signup(request);

        // then
        var savedUser = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail()).orElseThrow();
        assertThat(savedUser.getAuth().getEmail()).isEqualTo(signup.getEmail());
    }

    @Test
    @DisplayName("이메일 중복 확인")
    void signup_fail_when_email_duplication() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest rq1 = createSignupRequest(email, password);
        SignupRequest rq2 = createSignupRequest(email, password);

        // when
        authService.signup(rq1);

        // then
        assertThatThrownBy(() -> authService.signup(rq2))
            .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("회원 탈퇴된 id 중복 확인")
    void signup_fail_withdraw_id() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest rq1 = createSignupRequest(email, password);
        SignupRequest rq2 = createSignupRequest(email, password);

        SignupResponse signup = authService.signup(rq1);

        User user = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UserWithdrawRequest userWithdrawRequest = new UserWithdrawRequest();
        ReflectionTestUtils.setField(userWithdrawRequest, "password", "Password123");

        String authHeader = jwtUtil.createAccessToken(user.getId(), user.getRole());

        ExternalApiResponse fakeProjectResponse = fakeProjectResponse();

        ExternalApiResponse fakeParticipationResponse = fakeParticipationResponse();

        when(projectApiClient.getProjectMyRole(anyString(), anyLong()))
            .thenReturn(fakeProjectResponse);

        when(participationApiClient.getSurveyStatus(anyString(), anyLong(), anyInt(), anyInt()))
            .thenReturn(fakeParticipationResponse);

        // when
        authService.withdraw(signup.getMemberId(), userWithdrawRequest, authHeader);

        // then
        assertThatThrownBy(() -> authService.signup(rq2))
            .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("모든 회원 조회 - 성공")
    void getAllUsers_success() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");
        SignupRequest rq2 = createSignupRequest("user@example1.com", "Password123");

        authService.signup(rq1);
        authService.signup(rq2);

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<UserInfoResponse> all = userService.getAll(pageable);

        // then
        assertThat(all.getContent()).hasSize(2);
        assertThat(all.getContent().get(0).getEmail()).isEqualTo("user@example1.com");
    }

    @Test
    @DisplayName("회원조회 - 성공 (프로필 조회)")
    void get_profile() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = authService.signup(rq1);

        User user = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UserInfoResponse member = UserInfoResponse.from(user);

        // when
        UserInfoResponse userInfoResponse = userService.getUser(member.getMemberId());

        // then
        assertThat(userInfoResponse.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("회원조회 - 실패 (프로필 조회)")
    void get_profile_fail() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        authService.signup(rq1);

        Long invalidId = 9999L;

        // then
        assertThatThrownBy(() -> userService.getUser(9999L))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("등급 조회 - 성공")
    void grade_success() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = authService.signup(rq1);

        User user = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UserInfoResponse member = UserInfoResponse.from(user);

        // when
        UserGradeResponse grade = userService.getGradeAndPoint(member.getMemberId());

        // then
        assertThat(grade.getGrade()).isEqualTo(Grade.valueOf("LV1"));
    }

    @Test
    @DisplayName("등급 조회 - 실패 (다른사람, 탈퇴한 회원)")
    void grade_fail() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        authService.signup(rq1);

        Long userId = 9999L;

        // then
        assertThatThrownBy(() -> userService.getGradeAndPoint(userId))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("등급을 조회 할 수 없습니다");
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void update_success() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = authService.signup(rq1);

        User user = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UpdateUserRequest request = updateRequest("홍길동2");

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

        //when
        UpdateUserResponse update = userService.update(request, user.getId());

        // then
        assertThat(update.getProfile().getName()).isEqualTo("홍길동2");
    }

    @Test
    @DisplayName("회원 정보 수정 - 실패(다른 Id, 존재하지 않은 ID)")
    void update_fail() {
        // given
        Long userId = 9999L;

        UpdateUserRequest request = updateRequest("홍길동2");

        // when & then
        assertThatThrownBy(() -> userService.update(request, userId))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("회원 탈퇴 - 성공")
    void withdraw_success() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = authService.signup(rq1);

        User user = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UserWithdrawRequest userWithdrawRequest = new UserWithdrawRequest();
        ReflectionTestUtils.setField(userWithdrawRequest, "password", "Password123");

        String authHeader = jwtUtil.createAccessToken(user.getId(), user.getRole());

        ExternalApiResponse fakeProjectResponse = fakeProjectResponse();

        ExternalApiResponse fakeParticipationResponse = fakeParticipationResponse();

        when(projectApiClient.getProjectMyRole(anyString(), anyLong()))
            .thenReturn(fakeProjectResponse);

        when(participationApiClient.getSurveyStatus(anyString(), anyLong(), anyInt(), anyInt()))
            .thenReturn(fakeParticipationResponse);

        // when
        authService.withdraw(user.getId(), userWithdrawRequest, authHeader);

        // then
        assertThatThrownBy(() -> authService.withdraw(signup.getMemberId(), userWithdrawRequest, authHeader))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다");

    }

    @Test
    @DisplayName("회원 탈퇴 - 실패 (탈퇴한 회원 = 존재하지 않은 ID)")
    void withdraw_fail() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = authService.signup(rq1);

        User user = userRepository.findByEmailAndIsDeletedFalse(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        user.delete();
        userRepository.save(user);

        UserWithdrawRequest userWithdrawRequest = new UserWithdrawRequest();
        ReflectionTestUtils.setField(userWithdrawRequest, "password", "Password123");

        String authHeader = "Bearer dummyAccessToken";

        // when & then
        assertThatThrownBy(() -> authService.withdraw(user.getId(), userWithdrawRequest, authHeader))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다");
    }

    private SignupRequest createSignupRequest(String email, String password) {
        SignupRequest.AuthRequest authRequest = new SignupRequest.AuthRequest();
        SignupRequest.ProfileRequest profileRequest = new SignupRequest.ProfileRequest();
        SignupRequest.AddressRequest addressRequest = new SignupRequest.AddressRequest();

        ReflectionTestUtils.setField(addressRequest, "province", "서울특별시");
        ReflectionTestUtils.setField(addressRequest, "district", "강남구");
        ReflectionTestUtils.setField(addressRequest, "detailAddress", "테헤란로 123");
        ReflectionTestUtils.setField(addressRequest, "postalCode", "06134");

        ReflectionTestUtils.setField(profileRequest, "name", "홍길동");
        ReflectionTestUtils.setField(profileRequest, "phoneNumber", "010-1234-5678");
        ReflectionTestUtils.setField(profileRequest, "nickName", "길동이123");
        ReflectionTestUtils.setField(profileRequest, "birthDate", LocalDateTime.parse("1990-01-01T09:00:00"));
        ReflectionTestUtils.setField(profileRequest, "gender", Gender.MALE);
        ReflectionTestUtils.setField(profileRequest, "address", addressRequest);


        ReflectionTestUtils.setField(authRequest, "email", email);
        ReflectionTestUtils.setField(authRequest, "password", password);
        ReflectionTestUtils.setField(authRequest, "provider", Provider.LOCAL);

        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "auth", authRequest);
        ReflectionTestUtils.setField(request, "profile", profileRequest);

        return request;
    }

    private UpdateUserRequest updateRequest(String name) {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();


        ReflectionTestUtils.setField(updateUserRequest, "password", null);
        ReflectionTestUtils.setField(updateUserRequest, "name", name);
        ReflectionTestUtils.setField(updateUserRequest, "phoneNumber", null);
        ReflectionTestUtils.setField(updateUserRequest, "nickName", null);
        ReflectionTestUtils.setField(updateUserRequest, "province", null);
        ReflectionTestUtils.setField(updateUserRequest, "district", null);
        ReflectionTestUtils.setField(updateUserRequest, "detailAddress", null);
        ReflectionTestUtils.setField(updateUserRequest, "postalCode", null);

        return updateUserRequest;
    }

    private ExternalApiResponse fakeProjectResponse() {
        ExternalApiResponse fakeProjectResponse = new ExternalApiResponse();
        ReflectionTestUtils.setField(fakeProjectResponse, "success", true);
        ReflectionTestUtils.setField(fakeProjectResponse, "data", List.of());

        return fakeProjectResponse;
    }

    private ExternalApiResponse fakeParticipationResponse() {
        Map<String, Object> fakeSurveyData = Map.of(
            "content", List.of(),
            "totalPages", 0
        );

        ExternalApiResponse fakeParticipationResponse = new ExternalApiResponse();
        ReflectionTestUtils.setField(fakeParticipationResponse, "success", true);
        ReflectionTestUtils.setField(fakeParticipationResponse, "data", fakeSurveyData);

        return fakeParticipationResponse;
    }
}
