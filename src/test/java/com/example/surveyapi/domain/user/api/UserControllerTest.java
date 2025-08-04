package com.example.surveyapi.domain.user.api;

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
import com.example.surveyapi.domain.user.application.dto.request.UpdateUserRequest;
import com.example.surveyapi.domain.user.application.dto.response.UserGradeResponse;
import com.example.surveyapi.domain.user.application.dto.response.UserInfoResponse;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;

import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    UserService userService;

    @Autowired
    ObjectMapper objectMapper;

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
    @DisplayName("회원 전체 조회 - 실패 (인증 안 됨)")
    void getAllUsers_fail_unauthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isUnauthorized());
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    @DisplayName("모든 회원 조회 - 성공")
    void getAllUsers_success() throws Exception {
        //given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        SignupRequest rq2 = createSignupRequest("user@example1.com");

        User user1 = create(rq1);
        User user2 = create(rq2);

        List<UserInfoResponse> users = List.of(
            UserInfoResponse.from(user1),
            UserInfoResponse.from(user2)
        );

        PageRequest pageable = PageRequest.of(0, 10);

        Page<UserInfoResponse> userPage = new PageImpl<>(users, pageable, users.size());

        given(userService.getAll(any(Pageable.class))).willReturn(userPage);

        // when * then
        mockMvc.perform(get("/api/v1/users?page=0&size=10"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content.length()").value(2))
            .andExpect(jsonPath("$.message").value("회원 전체 조회 성공"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    @DisplayName("모든 회원 조회 - 실패 (인원이 맞지 않을 때)")
    void getAllUsers_fail() throws Exception {
        //given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        SignupRequest rq2 = createSignupRequest("user@example1.com");

        User user1 = create(rq1);
        User user2 = create(rq2);

        List<UserInfoResponse> users = List.of(
            UserInfoResponse.from(user1),
            UserInfoResponse.from(user2)
        );
        given(userService.getAll(any(Pageable.class)))
            .willThrow(new CustomException(CustomErrorCode.USER_LIST_EMPTY));

        // when * then
        mockMvc.perform(get("/api/v1/users?page=0&size=10"))
            .andExpect(status().isInternalServerError());
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    @DisplayName("회원조회 - 성공 (프로필 조회)")
    void get_profile() throws Exception {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        User user = create(rq1);

        UserInfoResponse member = UserInfoResponse.from(user);

        given(userService.getUser(user.getId())).willReturn(member);

        // then
        mockMvc.perform(get("/api/v1/users/me"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.name").value("홍길동"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
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

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    @DisplayName("등급 조회 - 성공")
    void grade_success() throws Exception {
        // given
        SignupRequest rq1 = createSignupRequest("user@example.com");
        User user = create(rq1);
        UserInfoResponse member = UserInfoResponse.from(user);
        UserGradeResponse grade = UserGradeResponse.from(user.getGrade());

        given(userService.getGrade(member.getMemberId()))
            .willReturn(grade);

        // when & then
        mockMvc.perform(get("/api/v1/users/grade"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.grade").value("LV1"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    @DisplayName("등급 조회 - 실패 (다른사람, 탈퇴한 회원)")
    void grade_fail() throws Exception {
        SignupRequest rq1 = createSignupRequest("user@example.com");
        User user = create(rq1);
        UserInfoResponse member = UserInfoResponse.from(user);

        given(userService.getGrade(member.getMemberId()))
            .willThrow(new CustomException(CustomErrorCode.USER_NOT_FOUND));

        // then
        mockMvc.perform(get("/api/v1/users/grade"))
            .andExpect(status().isNotFound());
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @DisplayName("회원정보 수정 - 실패 (@Valid 유효성 검사)")
    @Test
    void updateUser_invalidRequest_returns400() throws Exception {
        // given
        String longName = "a".repeat(21);
        UpdateUserRequest invalidRequest = updateRequest(longName);
        // when & then
        mockMvc.perform(patch("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("요청 데이터 검증에 실패하였습니다."));
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

    private User create(SignupRequest request) {

        return User.create(
            request.getAuth().getEmail(),
            request.getAuth().getPassword(),
            request.getProfile().getName(),
            request.getProfile().getBirthDate(),
            request.getProfile().getGender(),
            request.getProfile().getAddress().getProvince(),
            request.getProfile().getAddress().getDistrict(),
            request.getProfile().getAddress().getDetailAddress(),
            request.getProfile().getAddress().getPostalCode()
        );
    }

    private UpdateUserRequest updateRequest(String name) {
        UpdateUserRequest updateUserRequest = new UpdateUserRequest();

        UpdateUserRequest.UpdateAuthRequest auth = new UpdateUserRequest.UpdateAuthRequest();
        ReflectionTestUtils.setField(auth, "password", null);

        UpdateUserRequest.UpdateAddressRequest address = new UpdateUserRequest.UpdateAddressRequest();
        ReflectionTestUtils.setField(address, "province", null);
        ReflectionTestUtils.setField(address, "district", null);
        ReflectionTestUtils.setField(address, "detailAddress", null);
        ReflectionTestUtils.setField(address, "postalCode", null);

        UpdateUserRequest.UpdateProfileRequest profile = new UpdateUserRequest.UpdateProfileRequest();
        ReflectionTestUtils.setField(profile, "name", name);
        ReflectionTestUtils.setField(profile, "address", address);

        ReflectionTestUtils.setField(updateUserRequest, "auth", auth);
        ReflectionTestUtils.setField(updateUserRequest, "profile", profile);

        return updateUserRequest;
    }
}
