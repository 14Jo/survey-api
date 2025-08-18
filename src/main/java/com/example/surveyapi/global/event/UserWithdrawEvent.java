package com.example.surveyapi.global.event;

import com.example.surveyapi.global.model.WithdrawEvent;
import lombok.Getter;

@Getter
public class UserWithdrawEvent implements WithdrawEvent {

    private final Long userId;

    public UserWithdrawEvent(Long userId) {
        this.userId = userId;
    }
}
