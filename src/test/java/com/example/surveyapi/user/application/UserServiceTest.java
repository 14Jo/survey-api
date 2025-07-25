package com.example.surveyapi.user.application;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MethodArgumentNotValidException;

import com.example.surveyapi.domain.user.application.dtos.request.auth.SignupRequest;
import com.example.surveyapi.domain.user.application.dtos.request.auth.WithdrawRequest;
import com.example.surveyapi.domain.user.application.dtos.request.vo.select.AddressRequest;
import com.example.surveyapi.domain.user.application.dtos.request.vo.select.AuthRequest;
import com.example.surveyapi.domain.user.application.dtos.request.vo.select.ProfileRequest;
import com.example.surveyapi.domain.user.application.dtos.response.auth.SignupResponse;
import com.example.surveyapi.domain.user.application.service.UserService;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.global.config.security.PasswordEncoder;
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
        SignupRequest request = createSignupRequest(email,password);

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
        ProfileRequest profileRequest = new ProfileRequest();
        AddressRequest addressRequest = new AddressRequest();

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
            .andExpect(result -> assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()));
    }
    
    @Test
    @DisplayName("비밀번호 암호화 확인")
    void signup_passwordEncoder(){

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest request = createSignupRequest(email, password);

        // when
        SignupResponse signup = userService.signup(request);

        // then
        var savedUser = userRepository.findByEmail(signup.getEmail()).orElseThrow();
        assertThat(passwordEncoder.matches("Password123",savedUser.getAuth().getPassword())).isTrue();
    }

    @Test
    @DisplayName("응답 Dto 반영 확인")
    void signup_response(){

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest request = createSignupRequest(email,password);

        // when
        SignupResponse signup = userService.signup(request);

        // then
        var savedUser = userRepository.findByEmail(signup.getEmail()).orElseThrow();
        assertThat(savedUser.getAuth().getEmail()).isEqualTo(signup.getEmail());
    }
    
    @Test
    @DisplayName("이메일 중복 확인")
    void signup_fail_when_email_duplication(){

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest rq1 = createSignupRequest(email,password);
        SignupRequest rq2 = createSignupRequest(email,password);

        // when
        userService.signup(rq1);

        // then
        assertThatThrownBy(() -> userService.signup(rq2))
            .isInstanceOf(CustomException.class);
    }
    
    @Test
    @DisplayName("회원 탈퇴된 id 중복 확인")
    void signup_fail_withdraw_id(){

        // given
        String email = "user@example.com";
        String password = "Password123";
        SignupRequest rq1 = createSignupRequest(email,password);
        SignupRequest rq2 = createSignupRequest(email,password);

        WithdrawRequest withdrawRequest = new WithdrawRequest();
        ReflectionTestUtils.setField(withdrawRequest, "password", "Password123");


        // when
        SignupResponse signup = userService.signup(rq1);
        userService.withdraw(signup.getUserId(), withdrawRequest);

        // then
        assertThatThrownBy(() -> userService.signup(rq2))
            .isInstanceOf(CustomException.class);
    }


    public static SignupRequest createSignupRequest(String email, String password) {
        AuthRequest authRequest = new AuthRequest();
        ProfileRequest profileRequest = new ProfileRequest();
        AddressRequest addressRequest = new AddressRequest();

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
}
