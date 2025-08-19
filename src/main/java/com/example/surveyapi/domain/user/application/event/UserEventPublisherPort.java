package com.example.surveyapi.domain.user.application.event;

import com.example.surveyapi.global.enums.EventCode;
import com.example.surveyapi.global.model.WithdrawEvent;

public interface UserEventPublisherPort {

    void publish(WithdrawEvent event, EventCode key);
}
