package com.example.surveyapi.domain.user.application.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.example.surveyapi.domain.survey.domain.survey.enums.SurveyStatus;
import com.example.surveyapi.domain.user.domain.user.User;
import com.example.surveyapi.domain.user.domain.user.UserRepository;
import com.example.surveyapi.global.enums.CustomErrorCode;
import com.example.surveyapi.global.event.SurveyActivateEvent;
import com.example.surveyapi.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventListener {

    private final UserRepository userRepository;

    @EventListener
    public void handlePointIncrease(SurveyActivateEvent event){
        try{
            log.info("설문 종료 Id - {} : ", event.getSurveyId());

            if(!event.getSurveyStatus().equals(SurveyStatus.CLOSED)){
                return;
            }
            User user = userRepository.findByIdAndIsDeletedFalse(event.getCreatorID())
                .orElseThrow(() -> new CustomException(CustomErrorCode.USER_NOT_FOUND));

            user.increasePoint();
        }catch (Exception e){
            log.error("포인트 상승 실패 , 등급 상승 실패 : {}", e.getMessage());
        }
    }
}
