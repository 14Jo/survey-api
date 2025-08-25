package com.example.surveyapi.domain.user.infra.event;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.user.application.event.UserEventListenerPort;
import com.example.surveyapi.global.event.RabbitConst;
import com.example.surveyapi.global.event.participation.ParticipationCreatedGlobalEvent;
import com.example.surveyapi.global.event.survey.SurveyActivateEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@RabbitListener(
    queues = RabbitConst.QUEUE_NAME_USER
)
public class UserConsumer {

    private final UserEventListenerPort userEventListenerPort;

    @RabbitHandler
    public void handleSurveyCompletion(SurveyActivateEvent event) {
        if(!"CLOSED".equals(event.getSurveyStatus()) ){
            return;
        }
        userEventListenerPort.surveyCompletion(event.getCreatorID());
    }

    @RabbitHandler
    public void handleParticipation(ParticipationCreatedGlobalEvent event) {
        userEventListenerPort.participation(event.getUserId());
    }
}
