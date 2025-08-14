package com.example.surveyapi.domain.user.application.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.surveyapi.domain.user.domain.user.event.UserEvent;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.event.UserWithdrawEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserEventPublisherPort rabbitPublisher;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(UserEvent domainEvent){
        UserWithdrawEvent globalEvent = objectMapper.convertValue(domainEvent, UserWithdrawEvent.class);
        rabbitPublisher.publish(globalEvent, EventCode.USER_WITHDRAW);
    }
}
