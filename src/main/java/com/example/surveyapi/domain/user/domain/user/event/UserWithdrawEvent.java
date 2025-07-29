package com.example.surveyapi.domain.user.domain.user.event;

import lombok.Getter;

@Getter
public class UserWithdrawEvent {

    private final Long userId;

    public UserWithdrawEvent(Long userId) {
        this.userId = userId;
    }
}
