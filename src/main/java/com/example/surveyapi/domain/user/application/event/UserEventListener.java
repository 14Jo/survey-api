package com.example.surveyapi.domain.user.application.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.user.domain.user.event.UserEvent;
import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.user.UserWithdrawEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventPublisherPort rabbitPublisher;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserEvent domainEvent){
        log.info("이벤트 발행 전 ");
        UserWithdrawEvent globalEvent = objectMapper.convertValue(domainEvent, UserWithdrawEvent.class);
        rabbitPublisher.publish(globalEvent, EventCode.USER_WITHDRAW);
        log.info("이벤트 발행 후 ");
    }
}
