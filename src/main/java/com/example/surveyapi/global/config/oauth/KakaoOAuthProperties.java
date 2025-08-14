package com.example.surveyapi.global.config.oauth;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * application.yml에 입력된 설정 값을 자바 객체에 매핑하기 위한 클래스
 * @ConfigurationProperties 사용하기 위해서 @Setter, @Getter 사용
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "oauth.kakao")
public class KakaoOAuthProperties {

    private String clientId;
    private String redirectUri;


}
