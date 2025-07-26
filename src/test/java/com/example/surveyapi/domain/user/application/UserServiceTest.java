package com.example.surveyapi.domain.user.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.surveyapi.domain.user.application.UserService;
import com.example.surveyapi.domain.user.application.dto.request.LoginRequest;
import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.request.UpdateRequest;
import com.example.surveyapi.domain.user.application.dto.request.WithdrawRequest;
import com.example.surveyapi.domain.user.application.dto.response.GradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.LoginResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.enums.Grade;
import com.example.surveyapi.global.config.security.PasswordEncoder;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "SECRET_KEY=qwenakdfknzknnl1oq12316adfkakadfj12315ndjhufd893d")
@Transactional
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원 가입 - 성공 (DB 저장 검증)")
    void signup_success() {

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest request = createSignupRequest(email, password);

        // when
        SignupResponse signup = userService.signup(request);

        // then
        var savedUser = userRepository.findByEmail(signup.getEmail()).orElseThrow();
        assertThat(savedUser.getProfile().getName()).isEqualTo("홍길동");
        assertThat(savedUser.getProfile().getAddress().getProvince()).isEqualTo("서울특별시");
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
        SignupResponse signup = userService.signup(request);

        // then
        var savedUser = userRepository.findByEmail(signup.getEmail()).orElseThrow();
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
        SignupResponse signup = userService.signup(request);

        // then
        var savedUser = userRepository.findByEmail(signup.getEmail()).orElseThrow();
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
        userService.signup(rq1);

        // then
        assertThatThrownBy(() -> userService.signup(rq2))
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

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        ReflectionTestUtils.setField(withdrawRequest, "password", "Password123");

        // when
        SignupResponse signup = userService.signup(rq1);
        userService.withdraw(signup.getMemberId(), withdrawRequest);

        // then
        assertThatThrownBy(() -> userService.signup(rq2))
            .isInstanceOf(CustomException.class);
    }

    @Test
    @DisplayName("모든 회원 조회 - 성공")
    void getAllUsers_success() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");
        SignupRequest rq2 = createSignupRequest("user@example1.com", "Password123");

        userService.signup(rq1);
        userService.signup(rq2);

        PageRequest pageable = PageRequest.of(0, 10);

        // when
        Page<UserResponse> all = userService.getAll(pageable);

        // then
        assertThat(all.getContent()).hasSize(2);
        assertThat(all.getContent().get(0).getEmail()).isEqualTo("user@example1.com");
    }

    @Test
    @DisplayName("회원조회 - 성공 (프로필 조회)")
    void get_profile() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = userService.signup(rq1);

        User user = userRepository.findByEmail(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UserResponse member = UserResponse.from(user);

        // when
        UserResponse userResponse = userService.getUser(member.getMemberId());

        // then
        assertThat(userResponse.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("회원조회 - 실패 (프로필 조회)")
    void get_profile_fail() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = userService.signup(rq1);

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

        SignupResponse signup = userService.signup(rq1);

        User user = userRepository.findByEmail(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UserResponse member = UserResponse.from(user);

        // when
        GradeResponse grade = userService.getGrade(member.getMemberId());

        // then
        assertThat(grade.getGrade()).isEqualTo(Grade.valueOf("LV1"));
    }

    @Test
    @DisplayName("등급 조회 - 실패 (다른사람, 탈퇴한 회원)")
    void grade_fail() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = userService.signup(rq1);

        Long userId = 9999L;

        // then
        assertThatThrownBy(() -> userService.getGrade(userId))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("회원 정보 수정 - 성공")
    void update_success() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = userService.signup(rq1);

        User user = userRepository.findByEmail(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        UpdateRequest request = updateRequest("홍길동2");

        UpdateRequest.UpdateData data = UpdateRequest.UpdateData.from(request);

        user.update(
            data.getPassword(), data.getName(),
            data.getProvince(), data.getDistrict(),
            data.getDetailAddress(), data.getPostalCode()
        );

        //when
        UserResponse update = userService.update(request, user.getId());

        // then
        assertThat(update.getName()).isEqualTo("홍길동2");
    }

    @Test
    @DisplayName("회원 정보 수정 - 실패(다른 Id, 존재하지 않은 ID)")
    void update_fail() {
        // given
        Long userId = 9999L;

        UpdateRequest request = updateRequest("홍길동2");

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

        SignupResponse signup = userService.signup(rq1);

        User user = userRepository.findByEmail(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        ReflectionTestUtils.setField(withdrawRequest, "password", "Password123");

        // when
        userService.withdraw(user.getId(), withdrawRequest);

        // then
        assertThatThrownBy(() -> userService.withdraw(signup.getMemberId(), withdrawRequest))
            .isInstanceOf(CustomException.class)
            .hasMessageContaining("유저를 찾을 수 없습니다");

    }

    @Test
    @DisplayName("회원 탈퇴 - 실패 (탈퇴한 회원 = 존재하지 않은 ID)")
    void withdraw_fail() {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com", "Password123");

        SignupResponse signup = userService.signup(rq1);

        User user = userRepository.findByEmail(signup.getEmail())
            .orElseThrow(() -> new CustomException(CustomErrorCode.EMAIL_NOT_FOUND));

        user.delete();
        userRepository.save(user);

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        ReflectionTestUtils.setField(withdrawRequest, "password", "Password123");

        // when & then
        assertThatThrownBy(() -> userService.withdraw(user.getId(), withdrawRequest))
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
        ReflectionTestUtils.setField(profileRequest, "birthDate", LocalDateTime.parse("1990-01-01T09:00:00"));
        ReflectionTestUtils.setField(profileRequest, "gender", Gender.MALE);
        ReflectionTestUtils.setField(profileRequest, "address", addressRequest);

        ReflectionTestUtils.setField(authRequest, "email", email);
        ReflectionTestUtils.setField(authRequest, "password", password);

        SignupRequest request = new SignupRequest();
        ReflectionTestUtils.setField(request, "auth", authRequest);
        ReflectionTestUtils.setField(request, "profile", profileRequest);

        return request;
    }

    private UpdateRequest updateRequest(String name) {
        UpdateRequest updateRequest = new UpdateRequest();

        UpdateRequest.UpdateAuthRequest auth = new UpdateRequest.UpdateAuthRequest();
        ReflectionTestUtils.setField(auth, "password", null);

        UpdateRequest.UpdateAddressRequest address = new UpdateRequest.UpdateAddressRequest();
        ReflectionTestUtils.setField(address, "province", null);
        ReflectionTestUtils.setField(address, "district", null);
        ReflectionTestUtils.setField(address, "detailAddress", null);
        ReflectionTestUtils.setField(address, "postalCode", null);

        UpdateRequest.UpdateProfileRequest profile = new UpdateRequest.UpdateProfileRequest();
        ReflectionTestUtils.setField(profile, "name", name);
        ReflectionTestUtils.setField(profile, "address", address);

        ReflectionTestUtils.setField(updateRequest, "auth", auth);
        ReflectionTestUtils.setField(updateRequest, "profile", profile);

        return updateRequest;
    }
}
