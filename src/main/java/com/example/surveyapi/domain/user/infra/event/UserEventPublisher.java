package com.example.surveyapi.domain.user.infra.event;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.example.surveyapi.global.constant.RabbitConst;
import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.WithdrawEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishEvent(WithdrawEvent event, EventCode key) {
        String routingKey = RabbitConst.ROUTING_KEY.replace("#", key.name());
        rabbitTemplate.convertAndSend(RabbitConst.EXCHANGE_NAME, routingKey, event);
    }
}
