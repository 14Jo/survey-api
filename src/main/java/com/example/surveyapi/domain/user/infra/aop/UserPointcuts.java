package com.example.surveyapi.domain.user.infra.aop;

import org.aspectj.lang.annotation.Pointcut;

public class UserPointcuts {

    @Pointcut("@annotation(com.example.surveyapi.domain.user.infra.annotation.UserWithdraw) && args(userId))")
    public void withdraw(Long userId) {
    }

}
