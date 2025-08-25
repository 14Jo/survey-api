package com.example.surveyapi.user.application.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class UpdateUserRequest {

    @Size(min = 6, max = 20, message = "비밀번호는 6자 이상 20자 이하이어야 합니다")
    private String password;

    @Size(max = 20, message = "이름은 최대 20자까지 가능합니다")
    private String name;

    @Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 숫자 10~11자리여야 합니다.")
    private String phoneNumber;

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9]+$", message = "닉네임은 한글, 영문, 숫자만 사용할 수 있습니다.")
    private String nickName;

    @Size(max = 50, message = "시/도는 최대 50자까지 가능합니다")
    private String province;

    @Size(max = 50, message = "구/군은 최대 50자까지 가능합니다")
    private String district;

    @Size(max = 100, message = "상세주소는 최대 100자까지 가능합니다")
    private String detailAddress;

    @Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자여야 합니다")
    private String postalCode;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class UpdateData {

        private String password;
        private String name;
        private String phoneNumber;
        private String nickName;
        private String province;
        private String district;
        private String detailAddress;
        private String postalCode;

        public static UpdateData of(UpdateUserRequest request, String newPassword) {

            UpdateData dto = new UpdateData();

            if(newPassword != null){
                dto.password = newPassword;
            }

            if(request.name != null){
                dto.name = request.name;
            }

            if(request.phoneNumber != null){
                dto.phoneNumber = request.phoneNumber;
            }

            if(request.nickName != null){
                dto.nickName = request.nickName;
            }

            if(request.province != null){
                dto.province = request.province;
            }

            if(request.district != null){
                dto.district = request.district;
            }

            if(request.detailAddress != null){
                dto.detailAddress = request.detailAddress;
            }

            if(request.postalCode != null){
                dto.postalCode = request.postalCode;
            }
            return dto;
        }
    }
}
