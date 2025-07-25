package com.example.surveyapi.user.api;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;

import com.example.surveyapi.domain.user.application.dtos.request.auth.SignupRequest;
import com.example.surveyapi.domain.user.application.dtos.response.auth.SignupResponse;
import com.example.surveyapi.domain.user.application.service.UserService;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.enums.Gender;
import com.example.surveyapi.domain.user.domain.user.vo.Address;
import com.example.surveyapi.domain.user.domain.user.vo.Auth;
import com.example.surveyapi.domain.user.domain.user.vo.Profile;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = "SECRET_KEY=qwenakdfknzknnl1oq12316adfkakadfj12315ndjhufd893d")
@WithMockUser(username = "testUser", roles = "USER")
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

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

        SignupResponse mockResponse = new SignupResponse(user);

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

}
