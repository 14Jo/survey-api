package com.example.surveyapi.user.application.dto.request;

import java.time.LocalDateTime;

import com.example.surveyapi.user.domain.auth.enums.Provider;
import com.example.surveyapi.user.domain.user.enums.Gender;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class SignupRequest {

    @Valid
    @NotNull(message = "인증 정보는 필수입니다.")
    private AuthRequest auth;

    @Valid
    @NotNull(message = "프로필 정보는 필수입니다.")
    private ProfileRequest profile;

    @Getter
    public static class AuthRequest {
        @Email(message = "이메일 형식이 잘못됐습니다")
        @NotBlank(message = "이메일은 필수입니다")
        private String email;

        @NotBlank(message = "비밀번호는 필수입니다")
        @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하이어야 합니다")
        private String password;

        @NotNull(message = "로그인 형식은 필수입니다.")
        private Provider provider;
    }

    @Getter
    public static class ProfileRequest {

        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 20, message = "이름은 최대 20자까지 가능합니다")
        private String name;

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(
            regexp = "^01[016789]-\\d{3,4}-\\d{4}$",
            message = "전화번호 형식은 010-1234-5678과 같아야 합니다."
        )
        private String phoneNumber;


        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
        @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.")
        private String nickName;

        @NotNull(message = "생년월일은 필수입니다.")
        private LocalDateTime birthDate;

        @NotNull(message = "성별은 필수입니다.")
        private Gender gender;

        @Valid
        @NotNull(message = "주소는 필수입니다.")
        private AddressRequest address;
    }

    @Getter
    public static class AddressRequest {

        @NotBlank(message = "시/도는 필수입니다.")
        @Size(max = 50, message = "시/도는 최대 50자까지 가능합니다")
        private String province;

        @NotBlank(message = "구/군은 필수입니다.")
        @Size(max = 50, message = "구/군은 최대 50자까지 가능합니다")
        private String district;

        @NotBlank(message = "상세주소는 필수입니다.")
        @Size(max = 100, message = "상세주소는 최대 100자까지 가능합니다")
        private String detailAddress;

        @NotBlank(message = "우편번호는 필수입니다.")
        @Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자여야 합니다")
        private String postalCode;

    }

}
