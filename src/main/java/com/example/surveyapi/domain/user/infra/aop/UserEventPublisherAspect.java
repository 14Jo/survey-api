package com.example.surveyapi.domain.user.infra.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.dto.request.UserWithdrawRequest;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class UserEventPublisherAspect {

    private final ApplicationEventPublisher eventPublisher;

    private final UserRepository userRepository;

    @Pointcut("@annotation(com.example.surveyapi.domain.user.infra.annotation.UserWithdraw) && args(userId,request,authHeader)")
    public void withdraw(Long userId, UserWithdrawRequest request, String authHeader) {
    }

    @AfterReturning(
        pointcut = "withdraw(userId, request, authHeader)",
        argNames = "userId,request,authHeader"
    )
    public void publishUserWithdrawEvent(Long userId, UserWithdrawRequest request, String authHeader) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        user.registerUserWithdrawEvent();
        log.info("이벤트 발행 전");
        eventPublisher.publishEvent(user.getUserWithdrawEvent());
        log.info("이벤트 발행 후");
        user.clearUserWithdrawEvent();
    }
}
