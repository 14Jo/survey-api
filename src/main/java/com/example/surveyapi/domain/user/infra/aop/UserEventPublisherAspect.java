package com.example.surveyapi.domain.user.infra.aop;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class UserEventPublisherAspect {

    private final ApplicationEventPublisher eventPublisher;

    private final UserRepository userRepository;

    @AfterReturning(pointcut = "com.example.surveyapi.domain.user.infra.aop.UserPointcuts.withdraw(userId)", argNames = "userId")
    public void publishUserWithdrawEvent(Long userId) {

        User user = userRepository.findByIdAndIsDeletedFalse(userId)
            .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

        user.registerUserWithdrawEvent();
        eventPublisher.publishEvent(user.getUserWithdrawEvent());
        user.clearUserWithdrawEvent();
    }
}
