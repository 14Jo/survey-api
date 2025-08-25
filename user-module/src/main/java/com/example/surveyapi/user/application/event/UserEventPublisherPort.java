package com.example.surveyapi.user.application.event;

import com.example.surveyapi.global.event.EventCode;
import com.example.surveyapi.global.event.user.WithdrawEvent;

public interface UserEventPublisherPort {

    void publish(WithdrawEvent event, EventCode key);
}
