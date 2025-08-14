package com.example.surveyapi.domain.user.infra.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.surveyapi.domain.user.application.event.UserEventPublisherPort;
import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.WithdrawEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublisher implements UserEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public void publish(WithdrawEvent event, EventCode key) {
        if(key.equals(EventCode.USER_WITHDRAW)){
            rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, RabbitConst.ROUTING_KEY_USER_WITHDRAW, event);
        }

    }
}
