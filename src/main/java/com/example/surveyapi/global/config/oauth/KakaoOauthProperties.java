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
public class KakaoOauthProperties {

    // 카카오 REST API 키
    private String clientId;

    // 카카오 로그인 후 인가 코드 리다이렉트 되는 내 서버 URI
    private String redirectUri;

    // 인가 코드를 토큰으로 바꾸기 위해 호출하는 URI
    private String tokenUri;

    // 액세스 토큰으로 사용자 정보를 가져오는 URI (provider_id, 동의항목 가지고 옴) (현재 : 동의항목 미선택)
    private String userInfoUri; 

}
