package com.example.surveyapi.domain.user.domain.auth;

import com.example.surveyapi.domain.user.domain.auth.enums.Provider;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.global.model.BaseEntity;
import com.example.surveyapi.global.util.MaskingUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Auth extends BaseEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String email;

    @Column
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "provider_id", unique = true)
    private String providerId;

    private Auth(
        User user, String email, String password,
        Provider provider, String providerId
    ) {
        this.user = user;
        this.email = email;
        this.password = password;
        this.provider = provider;
        this.providerId = providerId;

    }

    public static Auth create(
        User user, String email, String password,
        Provider provider, String providerId
    ) {
        Auth auth = new Auth();
        auth.user = user;
        auth.email = email;
        auth.password = password;
        auth.provider = provider;
        auth.providerId = providerId;

        return auth;
    }

    public void updateProviderId(String providerId) {
        this.providerId = providerId;
    }

    public void updateAuth(String newPassword) {
        if (password != null) {
            this.password = newPassword;
        }
    }

    public void masking() {
        this.email = MaskingUtils.maskEmail(email, user.getId());
    }
}
