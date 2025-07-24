package com.example.surveyapi.domain.user.application.dtos.request.vo.select;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class AddressRequest {

    @NotBlank(message = "시/도는 필수입니다.")
    private String province;

    @NotBlank(message = "구/군은 필수입니다.")
    private String district;

    @NotBlank(message = "상세주소는 필수입니다.")
    private String detailAddress;

    @NotBlank(message = "우편번호는 필수입니다.")
    private String postalCode;


}
