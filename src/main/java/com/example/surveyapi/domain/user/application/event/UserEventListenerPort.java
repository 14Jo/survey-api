package com.example.surveyapi.domain.user.application.event;

public interface UserEventListenerPort {

    void surveyCompletion(Long userId);

    void participation(Long userId);
}
