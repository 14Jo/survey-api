package com.example.surveyapi.domain.user.application.event;

import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.user.application.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserHandlerEvent implements UserEventListenerPort {
    private final UserService userService;

    @Override
    public void surveyCompletion(Long userId) {
        try {
            log.info("설문 종료");
            userService.updatePoint(userId);
            log.info("포인트 상승");
        } catch (Exception e) {
            log.error("포인트 상승 실패 , 등급 상승 실패 : {}", e.getMessage());
        }
    }

    @Override
    public void participation(Long userId) {
        try {
            log.info("참여 완료");
            userService.updatePoint(userId);
            log.info("참여자 포인트 상승");
        } catch (Exception e) {
            log.error("참여자 포인트 상승 실패 , 등급 상승 실패 : {}", e.getMessage());
        }
    }
}
