package com.example.surveyapi.user.application.event;

public interface UserEventListenerPort {

    void surveyCompletion(Long userId);

    void participation(Long userId);
}
