package com.example.surveyapi.domain.user.infra.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.infra.event.UserEventPublisher;
import com.example.surveyapi.global.enums.EventCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserEventPublisherAspect {

    private final UserEventPublisher eventPublisher;

    @Pointcut("@annotation(com.example.surveyapi.domain.user.infra.annotation.UserWithdraw) && args(user)")
    public void withdraw(User user) {
    }

    @AfterReturning(pointcut = "withdraw(user)", argNames = "user")
    public void publishUserWithdrawEvent(User user) {

        user.registerUserWithdrawEvent();
        log.info("이벤트 발행 전");
        eventPublisher.publishEvent(user.pollUserWithdrawEvent(), EventCode.USER_WITHDRAW);
        log.info("이벤트 발행 후");
    }
}
