package com.example.surveyapi.user.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.util.List;

import com.example.surveyapi.domain.user.application.UserService;
import com.example.surveyapi.domain.user.application.dto.request.SignupRequest;
import com.example.surveyapi.domain.user.application.dto.response.GradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.SignupResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Auth;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "SECRET_KEY=qwenakdfknzknnl1oq12316adfkakadfj12315ndjhufd893d")
@WithMockUser(username = "testUser", roles = "USER")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Test
    @DisplayName("회원가입 - 성공")
    void signup_success() throws Exception {
        //given
        String requestJson = """
            {
              "auth": {
                "email": "user@example.com",
                "password": "Password123"
              },
              "profile": {
                "name": "홍길동",
                "birthDate": "1990-01-01T09:00:00",
                "gender": "MALE",
                "address": {
                  "province": "서울특별시",
                  "district": "강남구",
                  "detailAddress": "테헤란로 123",
                  "postalCode": "06134"
                }
              }
            }
            """;

        User user = new User(
            new Auth("user@example.com", "Password123"),
            new Profile("홍길동",
                LocalDateTime.parse("1990-01-01T09:00:00"),
                Gender.MALE,
                new Address("서울특별시",
                    "강남구",
                    "테헤란로 123",
                    "06134")));

        SignupResponse mockResponse = SignupResponse.from(user);

        given(userService.signup(any(SignupRequest.class))).willReturn(mockResponse);

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("회원가입 성공"));

    }

    @Test
    @DisplayName("회원가입 - 실패 (이메일 유효성 검사)")
    void signup_fail_email() throws Exception {
        // given
        String requestJson = """
            {
              "auth": {
                "email": "",
                "password": "Password123"
              },
              "profile": {
                "name": "홍길동",
                "birthDate": "1990-01-01T09:00:00",
                "gender": "MALE",
                "address": {
                  "province": "서울특별시",
                  "district": "강남구",
                  "detailAddress": "테헤란로 123",
                  "postalCode": "06134"
                }
              }
            }
            """;

        // when & then
        mockMvc.perform(post("/api/v1/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("모든 회원 조회 - 성공")
    void getAllUsers_success() throws Exception {
        //given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        SignupRequest rq2 = createSignupRequest("user@example1.com");

        User user1 = create(rq1);
        User user2 = create(rq2);

        List<UserResponse> users = List.of(
            UserResponse.from(user1),
            UserResponse.from(user2)
        );

        PageRequest pageable = PageRequest.of(0, 10);

        Page<UserResponse> userPage = new PageImpl<>(users, pageable, users.size());

        given(userService.getAll(any(Pageable.class))).willReturn(userPage);

        // when * then
        mockMvc.perform(get("/api/v1/users?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content.length()").value(2))
            .andExpect(jsonPath("$.message").value("회원 전체 조회 성공"));
    }

    @Test
    @DisplayName("모든 회원 조회 - 실패 (인원이 맞지 않을 때)")
    void getAllUsers_fail() throws Exception {
        //given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        SignupRequest rq2 = createSignupRequest("user@example1.com");

        User user1 = create(rq1);
        User user2 = create(rq2);

        List<UserResponse> users = List.of(
            UserResponse.from(user1),
            UserResponse.from(user2)
        );

        given(userService.getAll(any(Pageable.class)))
            .willThrow(new CustomException(CustomErrorCode.USER_LIST_EMPTY));

        // when * then
        mockMvc.perform(get("/api/v1/users?page=0&size=10"))
            .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("회원조회 - 성공 (프로필 조회)")
    void get_profile() throws Exception {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        User user = create(rq1);

        UserResponse member = UserResponse.from(user);

        given(userService.getUser(user.getId())).willReturn(member);

        // then
        mockMvc.perform(get("/api/v1/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("홍길동"));
    }

    @Test
    @DisplayName("회원조회 - 실패 (프로필 조회)")
    void get_profile_fail() throws Exception {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        User user = create(rq1);

        given(userService.getUser(user.getId()))
            .willThrow(new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // then
        mockMvc.perform(get("/api/v1/users/me"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("등급 조회 - 성공")
    void grade_success() throws Exception {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        User user = create(rq1);
        UserResponse member = UserResponse.from(user);
        GradeResponse grade = GradeResponse.from(user);

        given(userService.getGrade(member.getMemberId()))
            .willReturn(grade);

        // when & then
        mockMvc.perform(get("/api/v1/users/grade"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.grade").value("LV1"));
    }

    @Test
    @DisplayName("등급 조회 - 실패 (다른사람, 탈퇴한 회원)")
    void grade_fail() throws Exception {
        SignupRequest rq1 = createSignupRequest("user@example.com");
        User user = create(rq1);
        UserResponse member = UserResponse.from(user);

        given(userService.getGrade(member.getMemberId()))
            .willThrow(new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // then
        mockMvc.perform(get("/api/v1/users/grade"))
            .andExpect(status().isNotFound());
    }

    private SignupRequest createSignupRequest(String email) {
        SignupRequest signupRequest = new SignupRequest();

        SignupRequest.AuthRequest auth = new SignupRequest.AuthRequest();
        ReflectionTestUtils.setField(auth, "email", email);
        ReflectionTestUtils.setField(auth, "password", "Password123");

        SignupRequest.AddressRequest address = new SignupRequest.AddressRequest();
        ReflectionTestUtils.setField(address, "province", "서울특별시");
        ReflectionTestUtils.setField(address, "district", "강남구");
        ReflectionTestUtils.setField(address, "detailAddress", "테헤란로 123");
        ReflectionTestUtils.setField(address, "postalCode", "06134");

        SignupRequest.ProfileRequest profile = new SignupRequest.ProfileRequest();
        ReflectionTestUtils.setField(profile, "name", "홍길동");
        ReflectionTestUtils.setField(profile, "birthDate", LocalDateTime.parse("1990-01-01T09:00:00"));
        ReflectionTestUtils.setField(profile, "gender", Gender.MALE);
        ReflectionTestUtils.setField(profile, "address", address);

        ReflectionTestUtils.setField(signupRequest, "auth", auth);
        ReflectionTestUtils.setField(signupRequest, "profile", profile);

        return signupRequest;
    }

    private User create(SignupRequest rq) {
        return User.create(
            rq.getAuth().getEmail(),
            "encryptedPassword1",
            rq.getProfile().getName(),
            rq.getProfile().getBirthDate(),
            rq.getProfile().getGender(),
            rq.getProfile().getAddress().getProvince(),
            rq.getProfile().getAddress().getDistrict(),
            rq.getProfile().getAddress().getDetailAddress(),
            rq.getProfile().getAddress().getPostalCode()
        );
    }
}
